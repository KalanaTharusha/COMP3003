package edu.curtin.saed.assignment1;

import javafx.scene.image.Image;

public class Wall {
    private int id;
    private double x;
    private double y;
    private int hits;
    private Image icon;

    public Wall(int id, double x, double y, int hits, Image icon) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.hits = hits;
        this.icon = icon;
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

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }
}
