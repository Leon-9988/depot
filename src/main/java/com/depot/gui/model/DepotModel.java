package com.depot.gui.model;

import com.depot.gui.view.DepotView;
import com.depot.pojo.*;
import com.depot.pojo.common.*;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DepotModel {
    private Manager manager;
    private CustomerTableModel customerTableModel;
    private ParcelTableModel parcelTableModel;
    private ProcessedParcelTableModel processedParcelTableModel;
    private String customerFilePath;
    private String parcelFilePath;
    
    public DepotModel() {
        this.manager = new Manager();
        this.customerTableModel = new CustomerTableModel(manager);
        this.parcelTableModel = new ParcelTableModel(manager.getWorker());
        this.processedParcelTableModel = new ProcessedParcelTableModel();
        updateTables();
    }
    
    public void initSystem(String customerFile, String parcelFile) {
        this.customerFilePath = customerFile;
        this.parcelFilePath = parcelFile;
        manager.initSystem(customerFile, parcelFile);
        updateTables();
    }
    
    public void processNextCustomer() {
        try {
            manager.processNextCustomer();
            updateTables();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("No customers in queue");
        }
    }
    
    public void addNewCustomer(String name, String[] parcelIds) {
        if (customerFilePath == null) {
            throw new IllegalStateException("Please initialize the system first");
        }
        manager.addNewCustomer(name, parcelIds, customerFilePath);
        updateTables();
    }
    
    public void addNewParcel(String id, int days, float weight, float length, float width, float height) {
        if (parcelFilePath == null) {
            throw new IllegalStateException("Please initialize the system first");
        }
        manager.addNewParcel(id, days, weight, length, width, height, parcelFilePath);
        updateTables();
    }
    
    public void searchParcelById(String id) {
        manager.searchParcelById(id);
        updateTables();
    }
    
    public void updateTables() {
        customerTableModel.updateData(getCustomerList());
        parcelTableModel.updateData(getParcelList());
        processedParcelTableModel.updateData(getProcessedParcelList());
    }
    
    private List<Customer> getCustomerList() {
        List<Customer> customers = new ArrayList<>();
        Customer current = manager.getCustomerQueue().getHead();
        while (current != null) {
            customers.add(current);
            current = (Customer) current.next;
        }
        return customers;
    }
    
    private List<Parcel> getParcelList() {
        return new ArrayList<>(manager.getParcelMap().getAllParcels().values());
    }
    
    public CustomerTableModel getCustomerTableModel() {
        return customerTableModel;
    }
    
    public ParcelTableModel getParcelTableModel() {
        return parcelTableModel;
    }
    
    public Manager getManager() {
        return manager;
    }
    
    public void updateWorkStatus(DepotView view) {
        Customer currentCustomer = manager.getWorker().getCurrentCustomer();
        Parcel currentParcel = manager.getWorker().getCurrentParcel();
        float currentFee = currentCustomer != null ? currentCustomer.getcost() : 0.0f;
        
        view.updateWorkStatus(
            currentCustomer != null ? currentCustomer.getName() : null,
            currentParcel,
            currentFee
        );
    }
    
    private List<ProcessedParcel> getProcessedParcelList() {
        List<ProcessedParcel> processedParcels = new ArrayList<>();
        for (String parcelId : manager.getProcessedParcels()) {
            processedParcels.add(new ProcessedParcel(parcelId));
        }
        return processedParcels;
    }
    
    public ProcessedParcelTableModel getProcessedParcelTableModel() {
        return processedParcelTableModel;
    }
    
    public void deleteCustomer(int sequenceNum) {
        manager.deleteCustomer(sequenceNum);
        updateTables();
    }
    
    public String getCustomerFilePath() {
        return customerFilePath;
    }
    
    public String getParcelFilePath() {
        return parcelFilePath;
    }
}

class CustomerTableModel extends AbstractTableModel {
    private List<Customer> customers = new ArrayList<>();
    private final String[] columnNames = {"Sequence", "Name", "Parcel IDs", "Estimated Fee"};
    private Manager manager;
    
    public CustomerTableModel(Manager manager) {
        this.manager = manager;
    }
    
    public void updateData(List<Customer> customers) {
        this.customers = customers;
        fireTableDataChanged();
    }
    
    @Override
    public int getRowCount() {
        return customers.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Customer customer = customers.get(rowIndex);
        switch (columnIndex) {
            case 0: return customer.getSequence_num();
            case 1: return customer.getName();
            case 2: 
                String[] parcelIds = customer.getparcels(0);
                return String.join(", ", parcelIds);
            case 3: return calculateTotalFee(customer);
            default: return null;
        }
    }
    
    private float calculateTotalFee(Customer customer) {
        float totalFee = 0;
        String[] parcelIds = customer.getparcels(0);
        for (String parcelId : parcelIds) {
            if (parcelId != null) {
                Parcel parcel = manager.getParcelMap().getParcel(parcelId);
                if (parcel != null) {
                    totalFee += manager.getWorker().calculateFee(parcel);
                }
            }
        }
        return totalFee;
    }
}

class ParcelTableModel extends AbstractTableModel {
    private List<Parcel> parcels = new ArrayList<>();
    private final String[] columnNames = {"Parcel ID", "Days", "Weight (kg)", "Dimensions (cm)", "Category"};
    private Worker worker;
    
    public ParcelTableModel(Worker worker) {
        this.worker = worker;
    }
    
    public void updateData(List<Parcel> parcels) {
        this.parcels = parcels;
        fireTableDataChanged();
    }
    
    @Override
    public int getRowCount() {
        return parcels.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Parcel parcel = parcels.get(rowIndex);
        switch (columnIndex) {
            case 0: return parcel.getID();
            case 1: return parcel.getDays();
            case 2: return String.format("%.1f", parcel.getWeight());
            case 3: return String.format("%.0f×%.0f×%.0f", 
                parcel.getDimension().getLength(),
                parcel.getDimension().getWidth(),
                parcel.getDimension().getHeight());
            case 4: return worker.classifyParcel(parcel);
            default: return null;
        }
    }
}

class ProcessedParcelTableModel extends AbstractTableModel {
    private List<ProcessedParcel> processedParcels = new ArrayList<>();
    private final String[] columnNames = {"Parcel ID", "Processing Time"};
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public void updateData(List<ProcessedParcel> processedParcels) {
        this.processedParcels = processedParcels;
        fireTableDataChanged();
    }
    
    @Override
    public int getRowCount() {
        return processedParcels.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ProcessedParcel parcel = processedParcels.get(rowIndex);
        switch (columnIndex) {
            case 0: return parcel.getParcelId();
            case 1: return parcel.getProcessTime().format(formatter);
            default: return null;
        }
    }
}

class ProcessedParcel {
    private String parcelId;
    private LocalDateTime processTime;
    
    public ProcessedParcel(String parcelId) {
        this.parcelId = parcelId;
        this.processTime = LocalDateTime.now();
    }
    
    public String getParcelId() { return parcelId; }
    public LocalDateTime getProcessTime() { return processTime; }
} 