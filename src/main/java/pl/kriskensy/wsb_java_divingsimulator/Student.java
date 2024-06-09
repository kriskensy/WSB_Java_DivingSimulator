package pl.kriskensy.wsb_java_divingsimulator;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Student {
    private int id;
    private String name;
    private String experience;
    private int tankSize;
    private double depth;
    private double airLevel;
    private double markerX;
    private double markerY;
    private double prevMarkerX;
    private List<Point2D> pathPoints = new ArrayList<>();

    public Student(int id, String name, String experience, int tankSize) {
        this.id = id;
        this.name = name;
        this.experience = experience;
        this.tankSize = tankSize;
        this.depth = 0.0;
        this.airLevel = tankSize;
        this.markerX = 50;
        this.markerY = 100;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getExperience() {
        return experience;
    }

    public int getTankSize() {
        return tankSize;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public double getAirLevel() {
        return airLevel;
    }

    public void setAirLevel(double airLevel) {
        this.airLevel = airLevel;
    }

    public double getMarkerX() {
        return markerX;
    }

    public void setMarkerX(double markerX) {
        this.markerX = markerX;
    }

    public double getMarkerY() {
        return markerY;
    }

    public void setMarkerY(double markerY) {
        this.markerY = markerY;
    }

    public double getPrevMarkerX() {
        return prevMarkerX;
    }

    public void setPrevMarkerX(double prevMarkerX) {
        this.prevMarkerX = prevMarkerX;
    }

    public List<Point2D> getPathPoints() {
        return pathPoints;
    }

    public void setPathPoints(List<Point2D> pathPoints) {
        this.pathPoints = pathPoints;
    }

    @Override
    public String toString() {
        return name + ", exp: " + experience + ", tank: " + tankSize + " liters.";
    }
}
