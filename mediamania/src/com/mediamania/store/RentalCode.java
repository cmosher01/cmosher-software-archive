package com.mediamania.store;

import java.math.BigDecimal;

public class RentalCode
{
    private String      code;
    private int         numberOfDays;
    private BigDecimal  cost;
    private BigDecimal  lateFeePerDay;
    
    private RentalCode()
    { }
    
    public RentalCode(String code, int days,
                      BigDecimal cost, BigDecimal lateFee) {
        this.code = code;
        numberOfDays = days;
        this.cost = cost;
        lateFeePerDay = lateFee;
    }
    public String getCode() {
        return code;
    }
    public int getNumberOfDays() {
        return numberOfDays;
    }
    public BigDecimal getCost() {
        return cost;
    }
    public BigDecimal getLateFeePerDay() {
        return lateFeePerDay;
    }
}