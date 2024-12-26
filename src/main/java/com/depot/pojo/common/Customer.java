package com.depot.pojo.common;
public class Customer extends ListNode {
    private int sequence_num;
    private String name;
    private String[] parcels;
    private float fee;
    
    public Customer(int sequence_num, String name) {
        this.sequence_num = sequence_num;
        this.name = name;
        this.parcels = new String[10]; // 初始化数组，假设每个客户最多10个包裹
        this.fee = 0.0f;
    }
    
    public String[] getparcels(int count) {
        if (count == 0) {
            return parcels;
        }
        String[] result = new String[count];
        System.arraycopy(parcels, 0, result, 0, Math.min(count, parcels.length));
        return result;
    }

    public void addParcel(String parcelId) {
        for (int i = 0; i < parcels.length; i++) {
            if (parcels[i] == null) {
                parcels[i] = parcelId;
                break;
            }
        }
    }
    
    // Getters and setters
    public int getSequence_num() { return sequence_num; }
    public String getName() { return name; }
    public float getcost() { return fee; }
    public void setFee(float fee) { this.fee = fee; }
} 
