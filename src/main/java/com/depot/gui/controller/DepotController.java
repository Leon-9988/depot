package com.depot.gui.controller;

import com.depot.gui.model.DepotModel;
import com.depot.gui.view.DepotView;
import com.depot.pojo.Log;

import javax.swing.*;

public class DepotController {
    private DepotModel model;
    private DepotView view;
    
    public DepotController(DepotModel model, DepotView view) {
        this.model = model;
        this.view = view;
        
        // 设置表格模型
        view.getCustomerTable().setModel(model.getCustomerTableModel());
        view.getParcelTable().setModel(model.getParcelTableModel());
        view.getProcessedParcelTable().setModel(model.getProcessedParcelTableModel());
        
        // 添加事件监听器
        initializeEventHandlers();
    }
    
    private void initializeEventHandlers() {
        view.getInitButton().addActionListener(e -> initSystem());
        view.getProcessButton().addActionListener(e -> processNextCustomer());
        view.getSearchButton().addActionListener(e -> searchParcel());
        view.getAddCustomerButton().addActionListener(e -> addCustomer());
        view.getAddParcelButton().addActionListener(e -> addParcel());
        view.getDeleteCustomerButton().addActionListener(e -> deleteCustomer());
    }
    
    private void initSystem() {
        JFileChooser fileChooser = new JFileChooser("src/main/resources");
        
        // Select customer file
        JOptionPane.showMessageDialog(view, "Please select customer data file");
        int result = fileChooser.showOpenDialog(view);
        if (result != JFileChooser.APPROVE_OPTION) return;
        String customerFile = fileChooser.getSelectedFile().getPath();
        
        // Select parcel file
        JOptionPane.showMessageDialog(view, "Please select parcel data file");
        result = fileChooser.showOpenDialog(view);
        if (result != JFileChooser.APPROVE_OPTION) return;
        String parcelFile = fileChooser.getSelectedFile().getPath();
        
        model.initSystem(customerFile, parcelFile);
        updateDisplay();
    }
    
    private void processNextCustomer() {
        try {
            model.processNextCustomer();
            updateDisplay();
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(view, 
                "No customers in queue", 
                "Error", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void searchParcel() {
        String id = JOptionPane.showInputDialog(view, "Please enter parcel ID:");
        if (id != null && !id.trim().isEmpty()) {
            model.searchParcelById(id);
            updateDisplay();
        }
    }
    
    private void addCustomer() {
        // Create input dialog
        JTextField nameField = new JTextField();
        JTextField parcelsField = new JTextField();
        
        Object[] message = {
            "Customer Name:", nameField,
            "Parcel IDs (separated by semicolons):", parcelsField
        };
        
        int option = JOptionPane.showConfirmDialog(view, message, "Add Customer", 
            JOptionPane.OK_CANCEL_OPTION);
            
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String[] parcelIds = parcelsField.getText().split(";");
            
            model.addNewCustomer(name, parcelIds);
            updateDisplay();
        }
    }
    
    private void addParcel() {
        // Create input dialog
        JTextField idField = new JTextField();
        JTextField daysField = new JTextField();
        JTextField weightField = new JTextField();
        JTextField lengthField = new JTextField();
        JTextField widthField = new JTextField();
        JTextField heightField = new JTextField();
        
        Object[] message = {
            "Parcel ID:", idField,
            "Storage Days:", daysField,
            "Weight:", weightField,
            "Length:", lengthField,
            "Width:", widthField,
            "Height:", heightField
        };
        
        int option = JOptionPane.showConfirmDialog(view, message, "Add Parcel", 
            JOptionPane.OK_CANCEL_OPTION);
            
        if (option == JOptionPane.OK_OPTION) {
            try {
                String id = idField.getText();
                int days = Integer.parseInt(daysField.getText());
                float weight = Float.parseFloat(weightField.getText());
                float length = Float.parseFloat(lengthField.getText());
                float width = Float.parseFloat(widthField.getText());
                float height = Float.parseFloat(heightField.getText());
                
                model.addNewParcel(id, days, weight, length, width, height);
                updateDisplay();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(view, "Please enter valid numbers");
            }
        }
    }
    
    private void deleteCustomer() {
        // Get selected row
        int selectedRow = view.getCustomerTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view,
                "Please select a customer to delete first",
                "Notice",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Get selected customer's sequence number
        int sequenceNum = (int) view.getCustomerTable().getValueAt(selectedRow, 0);
        
        // Confirm deletion
        int option = JOptionPane.showConfirmDialog(view,
            "Are you sure you want to delete customer with sequence number " + sequenceNum + "?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            try {
                model.deleteCustomer(sequenceNum);
                updateDisplay();
                JOptionPane.showMessageDialog(view,
                    "Customer deleted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(view,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateDisplay() {
        updateLogArea();
        model.updateWorkStatus(view);
    }
    
    private void updateLogArea() {
        view.getLogArea().setText(Log.getInstance().getLog());
    }
} 