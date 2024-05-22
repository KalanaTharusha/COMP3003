package edu.curtin.saed.assignment1;

public class Coords {
    private double x;
    private double y;
    private boolean occupied;

    public Coords(double x, double y, boolean occupied) {
        this.x = x;
        this.y = y;
        this.occupied = occupied;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}
