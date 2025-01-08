package com.depot.pojo.common;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private int sequenceNum;
    private String name;
    private List<String> parcels;
    private float fee;
    
    public Customer(int sequenceNum, String name) {
        this.sequenceNum = sequenceNum;
        this.name = name;
        this.parcels = new ArrayList<>();
        this.fee = 0.0f;
    }
    
    public void addParcel(String parcelId) {
        parcels.add(parcelId);
    }
    
    public String[] getParcels(int count) {
        if (count == 0) {
            return parcels.toArray(new String[0]);
        }
        String[] result = new String[Math.min(count, parcels.size())];
        for (int i = 0; i < result.length; i++) {
            result[i] = parcels.get(i);
        }
        return result;
    }
    
    // Getters and setters
    public int getSequenceNum() { return sequenceNum; }
    public String getName() { return name; }
    public float getCost() { return fee; }
    public void setFee(float fee) { this.fee = fee; }
} 
