package com.mediamania.store;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.mediamania.content.MediaContent;

public class MediaItem {
    private MediaContent    content;
    private String          format;
    private BigDecimal      purchasePrice;
    private RentalCode      rentalCode;
    private Set             rentalItems; // RentalItem
    private int             quantityInStockForPurchase;
    private int             soldYTD;
    private int             rentedYTD;
    
    private MediaItem()
    { }
    
    public MediaItem(MediaContent content, String format, BigDecimal price,
                     RentalCode rentalCode, int number4sale) {
        this.content = content;
        content.addMediaItem(this);
        this.format = format;
        purchasePrice = price;
        this.rentalCode = rentalCode;
        rentalItems = new HashSet();
        quantityInStockForPurchase = number4sale;
        soldYTD = 0;
        rentedYTD = 0;
    }
    public MediaContent getMediaContent() {
        return content;
    }
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }
    public String getFormat() {
        return format;
    }
    public RentalCode getRentalCode() {
        return rentalCode;
    }
    public void setRentalCode(RentalCode code) {
        rentalCode = code;
    }
    public void addRentalItem(RentalItem rentalItem) {
        rentalItems.add(rentalItem);
    }
    public Set getRentalItems() {
        return Collections.unmodifiableSet(rentalItems);
    }
    public void sold(int qty) {
        if (qty > quantityInStockForPurchase) {
            // report error
        }
        quantityInStockForPurchase -= qty;
        soldYTD += qty;
    }
}