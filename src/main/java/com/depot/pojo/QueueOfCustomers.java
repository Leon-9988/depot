package com.depot.pojo;

import com.depot.pojo.common.Customer;

public class QueueOfCustomers {
    private Customer head;
    private int size;
    
    public QueueOfCustomers() {
        head = null;
        size = 0;
    }

    
    public void addCustomer(Customer customer) {
        if (head == null) {
            head = customer;
        } else {
            Customer current = head;
            while (current.next != null) {
                current = (Customer) current.next;
            }
            current.next = customer;
        }
        size++;
    }
    
    public Customer removeCustomer() {
        if (head == null) {
            return null;
        }
        Customer customer = head;
        head = (Customer) head.next;
        customer.next = null;
        size--;
        return customer;
    }
    
    public Customer getHead() {
        return head;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setHead(Customer customer) {
        this.head = customer;
    }
    
    public void decreaseSize() {
        if (size > 0) {
            size--;
        }
    }
} 