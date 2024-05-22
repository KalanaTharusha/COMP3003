package edu.curtin.saed.assignment1;

import javafx.scene.image.Image;

import java.util.List;

public class Robot {
    private int id;
    private double x;
    private double y;
    private Coords coords;
    private long delayedValue;
    private boolean alive;
    private Image icon;

    public Robot(int id, double x, double y, long delayedValue, Image icon) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.delayedValue = delayedValue;
        this.alive = true;
        this.icon = icon;
    }

    // Move robot up
    public void moveUp(List<Coords> grid, Coords spawnCoords) {
        double currY = this.y;
        double nextY;
        Coords currCoords = null;
        Coords nextCoords = null;

        if (this.y>0) {
            nextY = currY - 1;
        }
        else {
            nextY = currY + 1;
        }

        if (isSpawnCoords(this.x, nextY)) {
            return;
        }

        for (Coords coordsGrid : grid) {
            if (coordsGrid.getX() == this.x && coordsGrid.getY() == currY) {
                currCoords = coordsGrid;
            }
            else if (coordsGrid.getX() == this.x && coordsGrid.getY() == nextY) {
                if (coordsGrid.isOccupied()) {
                    return;
                }
                nextCoords = coordsGrid;
            }
        }
        nextCoords.setOccupied(true);
        this.y = nextY;
        currCoords.setOccupied(false);
        if (spawnCoords.getX() == this.x && spawnCoords.getY() == currY) {
            spawnCoords.setOccupied(false);
        }

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Move robot down
    public void moveDown(List<Coords> grid, Coords spawnCoords) {
        double currY = this.y;
        double nextY;
        Coords currCoords = null;
        Coords nextCoords = null;

        if (this.y<8) {
            nextY = currY + 1;
        }
        else {
            nextY = currY - 1;
        }

        if (isSpawnCoords(this.x, nextY)) {
            return;
        }

        for (Coords coordsGrid : grid) {
            if (coordsGrid.getX() == this.x && coordsGrid.getY() == currY) {
                currCoords = coordsGrid;
            }
            else if (coordsGrid.getX() == this.x && coordsGrid.getY() == nextY) {
                if (coordsGrid.isOccupied()) {
                    return;
                }
                nextCoords = coordsGrid;
            }
        }
        nextCoords.setOccupied(true);
        this.y = nextY;
        currCoords.setOccupied(false);
        if (spawnCoords.getX() == this.x && spawnCoords.getY() == currY) {
            spawnCoords.setOccupied(false);
        }

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Move robot right
    public void moveRight(List<Coords> grid, Coords spawnCoords) {
        double currX = this.x;
        double nextX;
        Coords currCoords = null;
        Coords nextCoords = null;

        if (this.x<8) {
            nextX = currX + 1;
        }
        else {
            nextX = currX - 1;
        }

        if (isSpawnCoords(nextX, this.y)) {
            return;
        }

        for (Coords coordsGrid : grid) {
            if (coordsGrid.getX() == currX && coordsGrid.getY() == this.y) {
                currCoords = coordsGrid;
            }
            else if (coordsGrid.getX() == nextX && coordsGrid.getY() == this.y) {
                if (coordsGrid.isOccupied()) {
                    return;
                }
                nextCoords = coordsGrid;
            }
        }
        nextCoords.setOccupied(true);
        this.x = nextX;
        currCoords.setOccupied(false);
        if (spawnCoords.getX() == currX && spawnCoords.getY() == this.y) {
            spawnCoords.setOccupied(false);
        }

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Move robot left
    public void moveLeft(List<Coords> grid, Coords spawnCoords) {
        double currX = this.x;
        double nextX;
        Coords currCoords = null;
        Coords nextCoords = null;

        if (this.x>0) {
            nextX = currX - 1;
        }
        else {
            nextX = currX + 1;
        }

        if (isSpawnCoords(nextX, this.y)) {
            return;
        }

        for (Coords coordsGrid : grid) {
            if (coordsGrid.getX() == currX && coordsGrid.getY() == this.y) {
                currCoords = coordsGrid;
            }
            else if (coordsGrid.getX() == nextX && coordsGrid.getY() == this.y) {
                if (coordsGrid.isOccupied()) {
                    return;
                }
                nextCoords = coordsGrid;
            }
        }
        nextCoords.setOccupied(true);
        this.x = nextX;
        currCoords.setOccupied(false);
        if (spawnCoords.getX() == currX && spawnCoords.getY() == this.y) {
            spawnCoords.setOccupied(false);
        }

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // return true if the robot collide with a wall
    public boolean hit(double wallX, double wallY) {
        return this.x == wallX && this.y == wallY;
    }

    // return true if it is not a spawn coordination
    private boolean isSpawnCoords(double x, double y) {
        boolean canMove = true;

        if ((x == 0.0 && y == 0.0) || (x == 0.0 && y == 8.0) || (x == 8.0 && y == 0.0) || (x == 8.0 && y == 8.0)) {
            canMove = false;
        }

        return !canMove;
    }

    //Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Coords getCoords() {
        return coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
    }

    public long getDelayedValue() {
        return delayedValue;
    }

    public void setDelayedValue(long delayedValue) {
        this.delayedValue = delayedValue;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }
}
