package com.mediamania.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Customer {
    private String  firstName;
    private String  lastName;
    private Address address;
    private String  phone;
    private String  email;
    private Set     currentRentals; // Rental
    private List    transactionHistory; // Transaction
    
    private Customer()
    { }
    public Customer(String firstName, String lastName, Address addr,
                    String phone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        address = addr;
        this.phone = phone;
        this.email = email;
        currentRentals = new HashSet();
        transactionHistory = new ArrayList();
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public Address getAddress() {
        return address;
    }
    public String getPhone() {
        return phone;
    }
    public String getEmail() {
        return email;
    }
    public void addRental(Rental rental){
        currentRentals.add(rental);
    }
    public Set getRentals() {
        return Collections.unmodifiableSet(currentRentals);
    }
    public void addTransaction(Transaction trans) {
        transactionHistory.add(trans);
    }
    public List getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }
}