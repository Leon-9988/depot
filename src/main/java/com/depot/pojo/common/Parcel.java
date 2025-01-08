package com.depot.pojo.common;

public class Parcel {
    private String ID;
    private int days;
    private float weight;
    private Dimension dimension;
    
    public Parcel(String ID, int days, float weight, Dimension dimension) {
        this.ID = ID;
        this.days = days;
        this.weight = weight;
        this.dimension = dimension;
    }
    
    public float getSize() {
        return dimension.getLength() * dimension.getWidth() * dimension.getHeight();
    }
    
    // Other getters and setters
    public String getID() { return ID; }
    public int getDays() { return days; }
    public float getWeight() { return weight; }
    public Dimension getDimension() { return dimension; }
} 
