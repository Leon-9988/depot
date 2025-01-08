package com.depot.pojo;

import com.depot.gui.controller.DepotController;
import com.depot.gui.model.DepotModel;
import com.depot.gui.view.DepotView;
import com.depot.pojo.common.Customer;
import com.depot.pojo.common.Dimension;
import com.depot.pojo.common.Parcel;

import javax.swing.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Manager {
    private QueOfCustomers customerQueue;
    private ParcelMap parcelMap;
    private Worker worker;
    private Set<String> processedParcels;
    private int currentSequenceNum = 1;
    
    public Manager() {
        this.customerQueue = new QueOfCustomers();
        this.parcelMap = new ParcelMap();
        this.worker = new Worker();
        this.processedParcels = new HashSet<>();
    }
    
    public void initSystem(String customerFile, String parcelFile) {
        loadCustomersFromFile(customerFile);
        loadParcelsFromFile(parcelFile);
        sortParcels();
        Log.getInstance().addLog("System initialization completed");
    }
    
    public Parcel searchParcelById(String id) {
        Parcel parcel = parcelMap.getParcel(id);
        if (parcel != null) {
            Log.getInstance().addLog("Search parcel: " + id);
        } else {
            Log.getInstance().addLog("Parcel not found: " + id);
        }
        return parcel;
    }
    
    public void updateParcelStatus(String id, boolean collected) {
        Parcel parcel = parcelMap.getParcel(id);
        if (parcel != null) {
            if (collected) {
                parcelMap.removeParcel(id);
            }
            Log.getInstance().addLog("Update parcel status: " + id + ", collected: " + collected);
        }
    }
    
    public void sortParcels() {
        List<Parcel> parcels = new ArrayList<>(parcelMap.getAllParcels().values());
        parcels.sort((p1, p2) -> Float.compare(p2.getSize(), p1.getSize()));
        
        // 清空并重新添加排序后的包裹
        parcelMap = new ParcelMap();
        for (Parcel parcel : parcels) {
            parcelMap.addParcel(parcel);
        }
        Log.getInstance().addLog("Parcels sorted by size");
    }
    
    public float getPerDayCost(LocalDate date) {
        float totalCost = 0;
        Log.getInstance().addLog("Calculate total cost for " + date);
        return totalCost;
    }
    
    public List<Parcel> getLongStoredParcels(int days) {
        List<Parcel> longStored = new ArrayList<>();
        for (Parcel parcel : parcelMap.getAllParcels().values()) {
            if (parcel.getDays() > days) {
                longStored.add(parcel);
            }
        }
        Log.getInstance().addLog("Count parcels stored more than " + days + " days: " + longStored.size() + " parcels");
        return longStored;
    }
    
    public void printReport(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=== Warehouse System Report ===");
            writer.println("Current customer count: " + customerQueue.getSize());
            writer.println("Current parcel count: " + parcelMap.getAllParcels().size());
            
            writer.println("\nLong-term stored parcels (>30 days):");
            List<Parcel> longStored = getLongStoredParcels(30);
            for (Parcel parcel : longStored) {
                writer.printf("ID: %s, Storage days: %d\n", parcel.getID(), parcel.getDays());
            }
            
            Log.getInstance().addLog("Report generated: " + filename);
        } catch (IOException e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }
    
    public void loadCustomersFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String name = parts[0].trim();
                    String[] parcelIds = parts[1].trim().split(";");
                    
                    // Auto-generate sequence number
                    int seqNum = generateSequenceNum();
                    
                    Customer customer = new Customer(seqNum, name);
                    for (String parcelId : parcelIds) {
                        if (!parcelId.trim().isEmpty()) {
                            customer.addParcel(parcelId.trim());
                        }
                    }
                    customerQueue.addCustomer(customer);
                }
            }
            Log.getInstance().addLog("Customers data loaded from file");
        } catch (IOException e) {
            System.err.println("Error reading customer file: " + e.getMessage());
        }
    }
    
    public void loadParcelsFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line ; // Skip CSV header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String id = parts[0].trim();
                    int days = Integer.parseInt(parts[1].trim());
                    float weight = Float.parseFloat(parts[2].trim());
                    float length = Float.parseFloat(parts[3].trim());
                    float width = Float.parseFloat(parts[4].trim());
                    float height = Float.parseFloat(parts[5].trim());
                    
                    Dimension dimension = new Dimension(length, width, height);
                    Parcel parcel = new Parcel(id, days, weight, dimension);
                    parcelMap.addParcel(parcel);
                }
            }
            Log.getInstance().addLog("Parcels data loaded from file");
        } catch (IOException e) {
            System.err.println("Error reading parcel file: " + e.getMessage());
        }
    }
    
    public void processNextCustomer() {
        Customer customer = customerQueue.removeCustomer();
        if (customer == null) {
            throw new IllegalStateException("No customers in queue");
        }
        
        String[] parcelIds = customer.getParcels(0);
        float totalFee = 0;
        
        for (String parcelId : parcelIds) {
            if (parcelId != null) {
                Parcel parcel = parcelMap.getParcel(parcelId);
                if (parcel != null) {
                    worker.processCustomer(customer, parcel);
                    parcelMap.removeParcel(parcelId);
                    processedParcels.add(parcelId);
                    totalFee += customer.getCost();
                }
            }
        }
        
        if (totalFee > 0) {
            Log.getInstance().addLog(String.format(
                "Processed all parcels for customer %s, Total cost: %.2f", 
                customer.getName(), totalFee));
        }
    }
    
    public boolean isParcelProcessed(String parcelId) {
        return processedParcels.contains(parcelId);
    }
    
    public QueOfCustomers getCustomerQueue() {
        return customerQueue;
    }
    
    public ParcelMap getParcelMap() {
        return parcelMap;
    }
    
    public Worker getWorker() {
        return worker;
    }
    
    public void addNewCustomer(String name, String[] parcelIds, String customerFilePath) {
        int seqNum = generateSequenceNum();
        Customer customer = new Customer(seqNum, name);
        for (String parcelId : parcelIds) {
            customer.addParcel(parcelId.trim());
        }
        customerQueue.addCustomer(customer);
        
        // Update specified customer file
        try (FileWriter fw = new FileWriter(customerFilePath, true)) {
            StringBuilder parcels = new StringBuilder();
            for (int i = 0; i < parcelIds.length; i++) {
                if (i > 0) parcels.append(";");
                parcels.append(parcelIds[i]);
            }
            fw.write(String.format("%s,%s\n", name, parcels.toString()));
            Log.getInstance().addLog("New customer added: " + name);
        } catch (IOException e) {
            System.err.println("Error updating customer file: " + e.getMessage());
        }
    }
    
    public void addNewParcel(String id, int days, float weight, float length, float width, float height, String customerId, String parcelFilePath) {
        // Create new parcel
        Dimension dimension = new Dimension(length, width, height);
        Parcel parcel = new Parcel(id, days, weight, dimension);
        parcelMap.addParcel(parcel);
        
        // If customer is specified, assign parcel to customer
        if (customerId != null && !customerId.isEmpty()) {
            Queue<Customer> tempQueue = new LinkedList<>(customerQueue.getCustomers());
            while (!tempQueue.isEmpty()) {
                Customer customer = tempQueue.poll();
                if (String.valueOf(customer.getSequenceNum()).equals(customerId)) {
                    customer.addParcel(id);
                    Log.getInstance().addLog(String.format("Parcel %s assigned to customer %s", id, customer.getName()));
                    break;
                }
            }
        }
        
        // Update parcel file
        try (FileWriter fw = new FileWriter(parcelFilePath, true)) {
            fw.write(String.format("%s,%d,%.1f,%.1f,%.1f,%.1f\n", 
                id, days, weight, length, width, height));
            Log.getInstance().addLog("New parcel added: " + id);
        } catch (IOException e) {
            System.err.println("Error updating parcel file: " + e.getMessage());
        }
    }
    
    private int generateSequenceNum() {
        return currentSequenceNum++;
    }
    
    public Set<String> getProcessedParcels() {
        return new HashSet<>(processedParcels); // Return a copy to protect internal state
    }
    
    public void deleteCustomer(int sequenceNum) {
        Queue<Customer> tempQueue = new LinkedList<>();
        Customer found = null;
        
        // Search for customer to delete
        while (!customerQueue.getCustomers().isEmpty()) {
            Customer customer = customerQueue.removeCustomer();
            if (customer.getSequenceNum() == sequenceNum) {
                found = customer;
            } else {
                tempQueue.offer(customer);
            }
        }
        
        // Return remaining customers to queue
        while (!tempQueue.isEmpty()) {
            customerQueue.addCustomer((Customer)tempQueue.poll());
        }
        
        if (found == null) {
            throw new IllegalArgumentException("Customer with specified sequence number not found: " + sequenceNum);
        }
        
        Log.getInstance().addLog(String.format("Customer %s (Sequence number: %d) deleted", 
            found.getName(), sequenceNum));
    }
    
    private List<Customer> getCustomerList() {
        List<Customer> customers = new ArrayList<>();
        Queue<Customer> tempQueue = new LinkedList<>(customerQueue.getCustomers());
        
        while (!tempQueue.isEmpty()) {
            customers.add(tempQueue.poll());
        }
        
        return customers;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DepotModel model = new DepotModel();
            DepotView view = new DepotView();
            DepotController controller = new DepotController(model, view);
            view.setVisible(true);
        });
    }
} 