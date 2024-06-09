package pl.kriskensy.wsb_java_divingsimulator;

import java.io.*;
import java.net.*;
import java.util.*;

public class DiveServer {
    private static final int PORT = 12345;
    private static Map<Integer, Double> tankLevels = new HashMap<>();
    private static Map<Integer, Double> depths = new HashMap<>();

    public static void main(String[] args) {

        List<Student> students = DatabaseHelper.getStudents();
        for (Student student : students) {
            tankLevels.put(student.getId(), (double) student.getTankSize());
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port: " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {

            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                String request;
                while ((request = in.readLine()) != null) {
                    if (request.startsWith("GET_TANK_LEVEL")) {
                        int studentId = Integer.parseInt(request.split(",")[1]);
                        double tankLevel = tankLevels.getOrDefault(studentId, 0.0);
                        out.println("TANK_LEVEL," + studentId + "," + tankLevel);
                    } else if (request.startsWith("UPDATE_DEPTH")) {
                        int studentId = Integer.parseInt(request.split(",")[1]);
                        double newDepth = Double.parseDouble(request.split(",")[2]);
                        depths.put(studentId, newDepth);
                        out.println("DEPTH_UPDATED," + studentId);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
