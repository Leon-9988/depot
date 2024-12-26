package com.depot.pojo;

import com.depot.pojo.common.Customer;
import com.depot.pojo.common.Parcel;



public class Worker {
    private Customer currentCustomer;
    private Parcel currentParcel;
    
    public float calculateFee(Parcel parcel) {
        float baseFee = parcel.getWeight() * 0.5f;
        
        float volumeFee = parcel.getDimension().getLength() * 
                         parcel.getDimension().getWidth() * 
                         parcel.getDimension().getHeight() * 0.001f;
        
        float storageFee = parcel.getDays() * 1.0f;
        
        return baseFee + volumeFee + storageFee;
    }
    
    public void processCustomer(Customer customer, Parcel parcel) {
        this.currentCustomer = customer;
        this.currentParcel = parcel;
        float fee = calculateFee(parcel);
        customer.setFee(fee);
        
        Log.getInstance().addLog(String.format(
            "处理客户[%s]的包裹[%s], 费用: %.2f",
            customer.getName(), parcel.getID(), fee));
    }
    
    public String classifyParcel(Parcel parcel) {
        float volume = parcel.getDimension().getLength() * 
                      parcel.getDimension().getWidth() * 
                      parcel.getDimension().getHeight();
        
        if (volume > 10000) return "大型包裹";
        else if (volume > 5000) return "中型包裹";
        else return "小型包裹";
    }
    
    public Customer getCurrentCustomer() {
        return currentCustomer;
    }
    
    public Parcel getCurrentParcel() {
        return currentParcel;
    }
} 