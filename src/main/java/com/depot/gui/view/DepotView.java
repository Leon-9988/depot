package com.depot.gui.view;

import com.depot.pojo.common.Parcel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class DepotView extends JFrame {
    private JTable customerTable;
    private JTable parcelTable;
    private JTable processedParcelTable;
    private JPanel currentWorkPanel;
    private JLabel currentCustomerLabel;
    private JLabel currentParcelLabel;
    private JLabel currentParcelDaysLabel;
    private JLabel currentParcelWeightLabel;
    private JLabel currentParcelDimensionLabel;
    private JLabel currentFeeLabel;
    private JButton initButton;
    private JButton processButton;
    private JButton searchButton;
    private JButton addCustomerButton;
    private JButton addParcelButton;
    private JButton deleteCustomerButton;
    private JTextArea logArea;
    
    public DepotView() {
        setTitle("Warehouse Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create left panel (customer queue and work status)
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        
        // Customer table
        customerTable = new JTable();
        JScrollPane customerScroll = new JScrollPane(customerTable);
        JPanel customerPanel = new JPanel(new BorderLayout());
        customerPanel.setBorder(new TitledBorder("Pending Customer Queue"));
        customerPanel.add(customerScroll);
        
        // Current work status panel
        currentWorkPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        currentWorkPanel.setBorder(new TitledBorder("Current Processing Status"));
        
        // Create information labels
        currentCustomerLabel = new JLabel("Current Customer: None");
        currentParcelLabel = new JLabel("Parcel ID: None");
        currentParcelDaysLabel = new JLabel("Storage Days: 0");
        currentParcelWeightLabel = new JLabel("Weight: 0.0 kg");
        currentParcelDimensionLabel = new JLabel("Dimensions: 0.0 x 0.0 x 0.0");
        currentFeeLabel = new JLabel("Fee: 0.00");
        
        // Set font
        Font labelFont = new Font("Arial", Font.PLAIN, 12);
        currentCustomerLabel.setFont(labelFont);
        currentParcelLabel.setFont(labelFont);
        currentParcelDaysLabel.setFont(labelFont);
        currentParcelWeightLabel.setFont(labelFont);
        currentParcelDimensionLabel.setFont(labelFont);
        currentFeeLabel.setFont(labelFont);
        
        // Add to panel
        currentWorkPanel.add(currentCustomerLabel);
        currentWorkPanel.add(currentParcelLabel);
        currentWorkPanel.add(currentParcelDaysLabel);
        currentWorkPanel.add(currentParcelWeightLabel);
        currentWorkPanel.add(currentParcelDimensionLabel);
        currentWorkPanel.add(currentFeeLabel);
        
        leftPanel.add(customerPanel, BorderLayout.CENTER);
        leftPanel.add(currentWorkPanel, BorderLayout.SOUTH);
        
        // Create right panel (includes pending and processed parcels)
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        
        // Pending parcel table
        parcelTable = new JTable();
        JScrollPane parcelScroll = new JScrollPane(parcelTable);
        JPanel parcelPanel = new JPanel(new BorderLayout());
        parcelPanel.setBorder(new TitledBorder("Pending Parcel List"));
        parcelPanel.add(parcelScroll);
        
        // Processed parcel table
        processedParcelTable = new JTable();
        JScrollPane processedParcelScroll = new JScrollPane(processedParcelTable);
        JPanel processedParcelPanel = new JPanel(new BorderLayout());
        processedParcelPanel.setBorder(new TitledBorder("Processed Parcel Records"));
        processedParcelPanel.add(processedParcelScroll);
        
        rightPanel.add(parcelPanel);
        rightPanel.add(processedParcelPanel);
        
        // Create split panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
            leftPanel, rightPanel);
        splitPane.setDividerLocation(450);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        initButton = new JButton("Initialize System");
        processButton = new JButton("Process Next Customer");
        searchButton = new JButton("Search Parcel");
        addCustomerButton = new JButton("Add Customer");
        addParcelButton = new JButton("Add Parcel");
//        deleteCustomerButton = new JButton("Delete Customer");
        
        buttonPanel.add(initButton);
        buttonPanel.add(processButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(addCustomerButton);
        buttonPanel.add(addParcelButton);
//        buttonPanel.add(deleteCustomerButton);
        
        // Create log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(0, 150));
        logScroll.setBorder(new TitledBorder("Operation Log"));
        
        // Add components to main panel
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(logScroll, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    // Getters
    public JTable getCustomerTable() { return customerTable; }
    public JTable getParcelTable() { return parcelTable; }
    public JTable getProcessedParcelTable() { return processedParcelTable; }
    public JButton getInitButton() { return initButton; }
    public JButton getProcessButton() { return processButton; }
    public JButton getSearchButton() { return searchButton; }
    public JButton getAddCustomerButton() { return addCustomerButton; }
    public JButton getAddParcelButton() { return addParcelButton; }
    public JButton getDeleteCustomerButton() { return deleteCustomerButton; }
    public JTextArea getLogArea() { return logArea; }
    
    // New: Method to update current work status
    public void updateWorkStatus(String customerName, Parcel parcel, float fee) {
        currentCustomerLabel.setText("Current Customer: " + (customerName != null ? customerName : "None"));
        
        if (parcel != null) {
            currentParcelLabel.setText("Parcel ID: " + parcel.getID());
            currentParcelDaysLabel.setText("Storage Days: " + parcel.getDays());
            currentParcelWeightLabel.setText(String.format("Weight: %.1f kg", parcel.getWeight()));
            currentParcelDimensionLabel.setText(String.format("Dimensions: %.1f x %.1f x %.1f",
                parcel.getDimension().getLength(),
                parcel.getDimension().getWidth(),
                parcel.getDimension().getHeight()));
        } else {
            currentParcelLabel.setText("Parcel ID: None");
            currentParcelDaysLabel.setText("Storage Days: 0");
            currentParcelWeightLabel.setText("Weight: 0.0 kg");
            currentParcelDimensionLabel.setText("Dimensions: 0.0 x 0.0 x 0.0");
        }
        
        currentFeeLabel.setText(String.format("Fee: %.2f", fee));
    }
} 