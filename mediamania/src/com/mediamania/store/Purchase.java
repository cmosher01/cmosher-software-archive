package com.mediamania.store;

import java.math.BigDecimal;
import java.util.Date;

public class Purchase extends Transaction {
    private MediaItem   mediaItem;
    
    private Purchase()
    { }    
    public Purchase(Customer cust, Date date, BigDecimal price, MediaItem item){
        super(cust, date);
        setPrice(price);
        mediaItem = item;
        price = item.getPurchasePrice();
    }   
    public MediaItem getMediaItem() {
        return mediaItem;
    }   
}