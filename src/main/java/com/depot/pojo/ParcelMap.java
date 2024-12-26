package com.depot.pojo;

import com.depot.pojo.common.Parcel;

import java.util.HashMap;
import java.util.Map;

public class ParcelMap {
    private Map<String, Parcel> parcels;
    
    public ParcelMap() {
        this.parcels = new HashMap<>();
    }
    
    public void addParcel(Parcel parcel) {
        parcels.put(parcel.getID(), parcel);
        Log.getInstance().addLog("New parcel added: " + parcel.getID());
    }
    
    public Parcel getParcel(String id) {
        return parcels.get(id);
    }
    
    public void removeParcel(String id) {
        Parcel parcel = parcels.remove(id);
        if (parcel != null) {
            Log.getInstance().addLog("Parcel processed: " + id);
        }
    }
    
    public Map<String, Parcel> getAllParcels() {
        return new HashMap<>(parcels);
    }
} 