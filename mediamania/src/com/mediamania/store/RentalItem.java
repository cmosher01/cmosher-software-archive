package com.mediamania.store;

public class RentalItem
{
    private MediaItem       mediaItem;
    private String          serialNumber;
    private Rental          currentRental;
    
    private RentalItem()
    { }
    public RentalItem(MediaItem item, String serialNum) {
        mediaItem = item;
        item.addRentalItem(this);
        serialNumber = serialNum;
        currentRental = null;
    }
    public MediaItem getMediaItem() {
        return mediaItem;
    }
    public Rental getCurrentRental() {
        return currentRental;
    }
    public void setCurrentRental(Rental rental) {
        currentRental = rental;
    }
}