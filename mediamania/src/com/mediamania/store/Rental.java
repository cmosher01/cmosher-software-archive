package com.mediamania.store;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Rental extends Transaction {
    private RentalItem      rentalItem;
    private RentalCode      rentalCode;
    private Date            returnDate;
    private Date            actualReturnDate;
    
    private Rental()
    { }
    
    public Rental(Customer cust, Date date, RentalItem item) {
        super(cust, date);
        rentalItem = item;
        item.setCurrentRental(this);
        rentalCode = item.getMediaItem().getRentalCode();
        setPrice(rentalCode.getCost());
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, rentalCode.getNumberOfDays());
        returnDate = cal.getTime();
        actualReturnDate = null;
    }
    public RentalItem getRentalItem() {
        return rentalItem;
    }
    public MediaItem getMediaItem() {
        return rentalItem.getMediaItem();
    }    
    public void setDateReturned(Date d) {
        actualReturnDate = d;
    }
}