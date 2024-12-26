package com.depot.pojo.common;

public class Parcel extends ListNode {
    private String ID;
    private int days;
    private float weight;
    private Dimension dimension;
    private Customer owner;
    
    public Parcel(String ID, int days, float weight, Dimension dimension) {
        this.ID = ID;
        this.days = days;
        this.weight = weight;
        this.dimension = dimension;
    }
    
    public Customer getowner() {
        return owner;
    }
    
    public float getsize() {
        return dimension.getLength() * dimension.getWidth() * dimension.getHeight();
    }
    
    // Other getters and setters
    public void setOwner(Customer owner) { this.owner = owner; }
    public String getID() { return ID; }
    public int getDays() { return days; }
    public float getWeight() { return weight; }
    public Dimension getDimension() { return dimension; }
} 
