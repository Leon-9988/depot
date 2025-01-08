package com.depot.pojo;

import com.depot.pojo.common.Customer;
import java.util.LinkedList;
import java.util.Queue;

public class QueOfCustomers {
    private Queue<Customer> customers;
    
    public QueOfCustomers() {
        customers = new LinkedList<>();
    }
    
    public void addCustomer(Customer customer) {
        customers.offer(customer);
    }
    
    public Customer removeCustomer() {
        return customers.poll();
    }
    
    public Customer getHead() {
        return customers.peek();
    }
    
    public int getSize() {
        return customers.size();
    }
    
    public Queue<Customer> getCustomers() {
        return new LinkedList<>(customers);
    }
} 