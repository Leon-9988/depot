package com.depot.pojo;

import com.depot.pojo.common.Customer;
import com.depot.pojo.common.Dimension;
import com.depot.pojo.common.Parcel;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Manager {
    private QueueOfCustomers customerQueue;
    private ParcelMap parcelMap;
    private Worker worker;
    private Set<String> processedParcels;
    private int currentSequenceNum = 1;
    
    public Manager() {
        this.customerQueue = new QueueOfCustomers();
        this.parcelMap = new ParcelMap();
        this.worker = new Worker();
        this.processedParcels = new HashSet<>();
    }
    
    public void initSystem(String customerFile, String parcelFile) {
        loadCustomersFromFile(customerFile);
        loadParcelsFromFile(parcelFile);
        sortCustomers();
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
    
    public void sortCustomers() {
        List<Customer> customers = new ArrayList<>();
        Customer current = customerQueue.getHead();
        while (current != null) {
            customers.add(current);
            current = (Customer) current.next;
        }
        
        customers.sort(Comparator.comparing(Customer::getName));
        Log.getInstance().addLog("Customer list sorted");
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
            String line ; // Skip CSV header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String name = parts[0].trim();
                    String[] parcelIds = parts[1].trim().split(";");
                    
                    // Auto-generate sequence number
                    int seqNum = generateSequenceNum();
                    
                    Customer customer = new Customer(seqNum, name);
                    for (String parcelId : parcelIds) {
                        customer.addParcel(parcelId.trim());
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
        
        String[] parcelIds = customer.getparcels(0);
        float totalFee = 0;
        
        for (String parcelId : parcelIds) {
            if (parcelId != null) {
                Parcel parcel = parcelMap.getParcel(parcelId);
                if (parcel != null) {
                    worker.processCustomer(customer, parcel);
                    parcelMap.removeParcel(parcelId);
                    processedParcels.add(parcelId);
                    totalFee += customer.getcost();
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
    
    public QueueOfCustomers getCustomerQueue() {
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
    
    public void addNewParcel(String id, int days, float weight, float length, float width, float height, String parcelFilePath) {
        Dimension dimension = new Dimension(length, width, height);
        Parcel parcel = new Parcel(id, days, weight, dimension);
        parcelMap.addParcel(parcel);
        
        // Update specified parcel file
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
        Customer current = customerQueue.getHead();
        Customer prev = null;
        
        while (current != null) {
            if (current.getSequence_num() == sequenceNum) {
                if (prev == null) {
                    // If it's the head of the queue
                    customerQueue.setHead((Customer)current.next);
                } else {
                    // If it's in the middle or at the end of the queue
                    prev.next = current.next;
                }
                customerQueue.decreaseSize();
                Log.getInstance().addLog(String.format("Customer %s (Sequence number: %d) deleted", 
                    current.getName(), sequenceNum));
                return;
            }
            prev = current;
            current = (Customer)current.next;
        }
        throw new IllegalArgumentException("Customer with specified sequence number not found: " + sequenceNum);
    }
    
    public static void main(String[] args) {
        Manager manager = new Manager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("=== Warehouse Management System ===");
            System.out.println("1. Load data from files");
            System.out.println("2. Add new customer");
            System.out.println("3. Add new parcel");
            System.out.println("4. Process next customer");
            System.out.println("5. Print report");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter customer data file path: ");
                    String customerFile = scanner.nextLine();
                    System.out.print("Enter parcel data file path: ");
                    String parcelFile = scanner.nextLine();
                    manager.initSystem(customerFile, parcelFile);
                    break;
                case 2:
                    System.out.print("Enter customer name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter parcel IDs (comma separated): ");
                    String[] parcelIds = scanner.nextLine().split(",");
                    //manager.addNewCustomer(name, parcelIds);
                    break;
                case 3:
                    System.out.print("Enter parcel ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Enter storage days: ");
                    int days = scanner.nextInt();
                    System.out.print("Enter weight: ");
                    float weight = scanner.nextFloat();
                    System.out.print("Enter dimensions (length, width, height): ");
                    float length = scanner.nextFloat();
                    float width = scanner.nextFloat();
                    float height = scanner.nextFloat();
                    //manager.addNewParcel(id, days, weight, length, width, height);
                    break;
                case 4:
                    try {
                        manager.processNextCustomer();
                    } catch (IllegalStateException e) {
                        System.out.println("No customers in queue.");
                    }
                    break;
                case 5:
                    System.out.print("Enter report filename: ");
                    String filename = scanner.nextLine();
                    manager.printReport(filename);
                    break;
                case 6:
                    System.out.println("Exiting system.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
} 