package com.mediamania.store;

import java.math.BigDecimal;
import java.util.Date;

public abstract class Transaction
{
    protected   Customer    customer;
    protected   Date        acquisitionDate;
    protected   BigDecimal  price;
    
    protected Transaction()
    { }
    protected Transaction(Customer cust, Date date) {
        customer = cust;
        acquisitionDate = date;
    }
    public abstract MediaItem getMediaItem();
    
    public Customer getCustomer() {
        return customer;
    }
    public Date getAcquisitionDate() {
        return acquisitionDate;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}