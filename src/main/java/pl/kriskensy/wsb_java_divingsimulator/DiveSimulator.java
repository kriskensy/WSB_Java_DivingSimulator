package pl.kriskensy.wsb_java_divingsimulator;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class represents the diving simulator application.
 */

public class DiveSimulator extends Application {

    //todo ograniczyc glebokosc poruszania sie od 100 (powierzchnia) bo mi wyskakuja nad wode :D
    //todo przycisk Reset

    private DiveClient client;
    private List<Student> selectedStudents = new ArrayList<>();
    private double instructorDepth = 0;
    private double instructorMarkerX = 60;
    private double instructorAirLevel = 10;
    private Canvas canvas;
    private GraphicsContext gc;
    private TextArea airInfoArea;
    private TextArea depthInfoArea;
    private Label timerLabel;
    private int countdown = 60;
    private List<Point2D> instructorPath = new ArrayList<>();
    private Random random = new Random();

    /**
     * Starts the application by setting up the primary stage.
     */

    @Override
    public void start(Stage primaryStage) throws IOException {
        DatabaseConnector.getConnection();

        client = new DiveClient("localhost", 12345);

        primaryStage.setTitle("Dive Simulator");

        BorderPane root = new BorderPane();
        VBox controlPanel = new VBox();
        controlPanel.setPadding(new Insets(10));
        controlPanel.setSpacing(10);

        ListView<Student> studentListView = new ListView<>();
        studentListView.getItems().addAll(DatabaseHelper.getStudents());
        studentListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        controlPanel.getChildren().add(new Label("Select 3 students:"));
        controlPanel.getChildren().add(studentListView);

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);

        Button startButton = new Button("Start Dive");
        Button randomStudentsButton = new Button("Random Students");
        Button instructorUpButton = new Button("Instructor Up");
        Button instructorDownButton = new Button("Instructor Down");
        timerLabel = new Label("Timer: 60");

        buttonBox.getChildren().addAll(startButton, randomStudentsButton, instructorUpButton, instructorDownButton, timerLabel);

        VBox generalButtons = new VBox();
        generalButtons.setSpacing(10);

        Button howToButton = new Button("How to?");
        //Button resetButton = new Button("Reset");
        Button exitButton = new Button("Exit");

        generalButtons.getChildren().addAll(howToButton, exitButton);

        controlPanel.getChildren().add(buttonBox);
        controlPanel.getChildren().add(generalButtons);
        root.setLeft(controlPanel);

        VBox displayPanel = new VBox();
        displayPanel.setPadding(new Insets(10));
        displayPanel.setSpacing(10);

        HBox informationPanel = new HBox();
        informationPanel.setSpacing(10);
        informationPanel.setPadding(new Insets(10));

        airInfoArea = new TextArea();
        airInfoArea.setEditable(false);
        airInfoArea.setPrefHeight(100);
        displayPanel.getChildren().add(airInfoArea);

        depthInfoArea = new TextArea();
        depthInfoArea.setEditable(false);
        depthInfoArea.setPrefHeight(100);
        displayPanel.getChildren().add(depthInfoArea);

        VBox airInfoPanel = new VBox();
        airInfoPanel.getChildren().addAll(new Label("Gas reserves:"), airInfoArea);

        VBox depthInfoPanel = new VBox();
        depthInfoPanel.getChildren().addAll(new Label("Current depth:"), depthInfoArea);

        informationPanel.getChildren().addAll(airInfoPanel, depthInfoPanel);
        displayPanel.getChildren().add(informationPanel);

        canvas = new Canvas(900, 400);
        gc = canvas.getGraphicsContext2D();
        displayPanel.getChildren().add(canvas);

        root.setCenter(displayPanel);

        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Platform.exit();
            }
        });

//        resetButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                resetState();
//            }
//        });

        howToButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("How to Use");
                alert.setHeaderText("How to Use the Dive Simulator");
                alert.setContentText("1. Select 3 students from the list or click 'Random Students' to randomly select 3 students.\n" +
                        "2. Click 'Start Dive' to begin the simulation.\n" +
                        "3. Use 'Instructor Up' and 'Instructor Down' to adjust the instructor's depth.\n" +
                        "4. The timer counts down from 60 seconds. After 60 seconds, all movements stop.\n" +
                        "5. Click 'Exit' to close the application.");
                alert.showAndWait();
            }
        });

        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedStudents.clear();
                selectedStudents.addAll(studentListView.getSelectionModel().getSelectedItems());
                if (selectedStudents.size() == 3) {
                    startDive();
                    startCountdownTimer();
                } else {
                    showAlert("Please select exactly 3 students.");
                }
            }
        });

        randomStudentsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectRandomStudents(studentListView);
            }
        });

        instructorUpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                instructorDepth = Math.min(instructorDepth + 0.5, 0);
                draw();
            }
        });

        instructorDownButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                instructorDepth = Math.max(instructorDepth - 0.5, -10);
                draw();
            }
        });

        Scene scene = new Scene(root, 1400, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        draw();
    }

    private boolean movementStopped = false;

    /**
     * Selects random students from a list and adds them to the simulation.
     */

    private void selectRandomStudents(ListView<Student> studentListView) {
        List<Student> allStudents = DatabaseHelper.getStudents();
        List<Integer> selectedIndexes = new ArrayList<>();

        studentListView.getSelectionModel().clearSelection();

        while (selectedIndexes.size() < 3) {
            int index = random.nextInt(allStudents.size());
            if (!selectedIndexes.contains(index)) {
                selectedIndexes.add(index);
            }
        }

        for (int index : selectedIndexes) {
            studentListView.getSelectionModel().select(index);
        }
    }

    /**
     * Starts the countdown to the end of the simulation.
     */

    private void startCountdownTimer() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    countdown--;
                    if (countdown >= 0) {
                        timerLabel.setText("Timer: " + countdown);
                    }
                    if (countdown == 0) {
                        stopMovement();
                    }
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Begins a simulation of a dive.
     */

    private void startDive() {
        movementStopped = false;
        new Thread(() -> {
            try {
                for (Student student : selectedStudents) {
                    student.getPathPoints().add(new Point2D(student.getMarkerX(), 100 - (student.getDepth() * 30)));
                }

                Timeline instructorTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    if (!movementStopped) {
                        updateDepths();
                        updateAirLevels();
                        draw();
                        moveInstructorMarker();
                    }
                }));
                instructorTimeline.setCycleCount(60);
                instructorTimeline.play();

                Thread.sleep(4000);

                Timeline studentsTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    if (!movementStopped) {
                        moveStudentsMarkers();
                    }
                }));
                studentsTimeline.setCycleCount(60);
                studentsTimeline.play();

                new Timeline(new KeyFrame(Duration.seconds(60), event -> stopMovement())).play();
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Stops the movement of the instructor and students.
     */

    private void stopMovement() {
        movementStopped = true;
    }

    /**
     * Moves student markers across the screen.
     */

    private void moveStudentsMarkers() {
        if (!movementStopped) {
            for (Student student : selectedStudents) {

//                double newMarkerY = student.getMarkerY();//todo to wyskakiwanie nad wode cos nie funkuje...:(
//                if (newMarkerY >= 100) {
//                    newMarkerY = 100;
//                }
//                student.setMarkerY(newMarkerY);
                student.setPrevMarkerX(student.getMarkerX());
                student.setMarkerX(student.getMarkerX() + 3);
                student.getPathPoints().add(new Point2D(student.getMarkerX(), 100 - (student.getDepth() * 20)));
            }
        }
    }

    /**
     * Moves the instructor's marker across the screen.
     */

    private void moveInstructorMarker() {
        if (!movementStopped) {
            instructorMarkerX += 3;
            double instructorY = 100 - (instructorDepth * 20);
            instructorPath.add(new Point2D(instructorMarkerX, instructorY));
        }
    }

    /**
     * Updates diving depths for students.
     */

    private void updateDepths() {
        for (Student student : selectedStudents) {
            double depthAdjustment = getDepthAdjustment(student.getExperience());
            double newDepth = instructorDepth + depthAdjustment;
            client.updateDepth(student.getId(), newDepth);
            student.setDepth(newDepth);
            student.setMarkerX(student.getMarkerX() + 10);
        }

        instructorMarkerX += 10;
    }

    /**
     * Draws the current state of the simulation on the screen.
     */

    private void draw() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);

        gc.strokeLine(50, 50, 50, 350);// Y-axis
        gc.strokeLine(50, 350, 850, 350);// X-axis

        double[] timePositions = {180, 314, 447, 579, 710, 843};
        String[] timeLabels = {"10s", "20s", "30s", "40s", "50s", "60s"};

        for (int i = 0; i < timePositions.length; i++) {
            double x = timePositions[i];
            gc.strokeLine(x, 350, x, 345); // Draw a short vertical line
            gc.fillText(timeLabels[i], x - 10, 365); // Draw the time label
        }

        drawHorizontalLineWithLabel(50, 150, 850, 150, "- 2,5 m", 0, 155);
        drawHorizontalLineWithLabel(50, 200, 850, 200, "- 5 m", 0, 205);
        drawHorizontalLineWithLabel(50, 250, 850, 250, "- 7,5 m", 0, 255);
        drawHorizontalLineWithLabel(50, 300, 850, 300, "- 10 m", 0, 305);

        drawSurfaceLineWithLabel(50, 100, 850, 100, "Surface", 0, 105);

        drawInstructor();
        drawStudents();
        drawLabels();
    }

    /**
     * Draws a surface line with a label.
     */

    private void drawSurfaceLineWithLabel(double startX, double startY, double endX, double endY, String label, double labelX, double labelY) {
        gc.setStroke(Color.BLUE);
        gc.strokeLine(startX, startY, endX, endY);
        gc.fillText(label, labelX, labelY);
    }

    /**
     * Draws a horizontal line with the label.
     */

    private void drawHorizontalLineWithLabel(double startX, double startY, double endX, double endY, String label, double labelX, double labelY) {
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.strokeLine(startX, startY, endX, endY);
        gc.fillText(label, labelX, labelY);
    }

    /**
     * Draws the instructor's marker and his path.
     */

    private void drawInstructor() {
        gc.setFill(Color.RED);
        double instructorY = 100 - (instructorDepth * 20);
        gc.fillOval(instructorMarkerX, instructorY, 12, 12);
        gc.setStroke(Color.RED);
        for (int i = 1; i < instructorPath.size(); i++) {
            Point2D prevPoint = instructorPath.get(i - 1);
            Point2D currentPoint = instructorPath.get(i);
            gc.strokeLine(prevPoint.getX(), prevPoint.getY(), currentPoint.getX(), currentPoint.getY());
        }
    }


    /**
     * Draws student markers and their paths.
     */

    private void drawStudents() {
        Color[] colors = {Color.BLUE, Color.GREEN, Color.GOLD};
        for (int i = 0; i < selectedStudents.size(); i++) {
            Student student = selectedStudents.get(i);
            double studentY = 100 - (student.getDepth() * 20);
            gc.setFill(colors[i]);
            gc.fillOval(student.getMarkerX(), studentY, 8, 8);

            List<Point2D> pathPoints = student.getPathPoints();
            if (pathPoints.size() > 1) {
                gc.setStroke(colors[i]);
                gc.setLineWidth(2);
                for (int j = 1; j < pathPoints.size(); j++) {
                    Point2D startPoint = pathPoints.get(j - 1);
                    Point2D endPoint = pathPoints.get(j);
                    gc.strokeLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
                }
            }
        }
    }

    /**
     * Draws labels for the instructor and students.
     */

    private void drawLabels() {

        gc.setFill(Color.RED);
        gc.fillText("Instructor", instructorMarkerX, 20);

        Color[] colors = {Color.BLUE, Color.GREEN, Color.GOLD};
        double labelOffset = 25;
        for (int i = 0; i < selectedStudents.size(); i++) {
            Student student = selectedStudents.get(i);
            gc.setFill(colors[i]);
            double labelY = student.getMarkerY() - labelOffset;
            gc.fillText(student.getName(), student.getMarkerX(), labelY);
            labelOffset += 20;
        }
    }

    /**
     * Calculates depth correction depending on the diver's experience.
     */

    private double getDepthAdjustment(String experience) {
        double adjustment = 0;
        switch (experience) {
            case "low":
                adjustment = Math.random() * 3 - 1.5; //+- 1.5m
                break;
            case "normal":
                adjustment = Math.random() * 2 - 1; //+- 1m
                break;
            case "high":
                adjustment = Math.random() * 1 - 0.5; //+- 0.5m
                break;
            case "very high":
                adjustment = Math.random() * 0.5 - 0.25; //+- 0.25m
                break;
        }
        return adjustment;
    }

    /**
     * Updates air levels for instructor and students.
     */

    private void updateAirLevels() {

        for (Student student : selectedStudents) {
            double airConsumption = 0.15;
            student.setAirLevel(student.getAirLevel() - airConsumption);
            if (student.getAirLevel() <= 0) {
                student.setAirLevel(0);
                student.setDepth(0);
            }
        }

        double airConsumption = 0.075;
        instructorAirLevel -= airConsumption;
        if (instructorAirLevel <= 0) {
            instructorAirLevel = 0;
            instructorDepth = 0;
        }

        updateAirInfo();
        updateDepthInfo();
    }

    /**
     * Updates air level information for instructor and students.
     */

    private void updateAirInfo() {
        StringBuilder airInfo = new StringBuilder();
        airInfo.append("Instructor: ").append(String.format("%.1f", instructorAirLevel)).append("L\n");
        for (Student student : selectedStudents) {
            airInfo.append(student.getName()).append(": ").append(String.format("%.1f", student.getAirLevel())).append("L\n");
        }
        airInfoArea.setText(airInfo.toString());
    }

    /**
     * Updates depth information for instructor and students.
     */

    private void updateDepthInfo() {
        StringBuilder depthInfo = new StringBuilder();
        depthInfo.append("Instructor depth: ").append(String.format("%.1f", instructorDepth)).append("m\n");
        for (Student student : selectedStudents) {
            depthInfo.append(student.getName()).append(" depth: ").append(String.format("%.1f", student.getDepth())).append("m\n");
        }
        depthInfoArea.setText(depthInfo.toString());
    }

    /**
     * Displays an alert message.
     */

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}