package com.mediamania.content;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.mediamania.store.MediaItem;

public abstract class MediaContent {
    private static SimpleDateFormat yearFmt = new SimpleDateFormat("yyyy");
    private String      title;
    private Studio      studio;
    private Date        releaseDate;
    private String      rating;
    private String      ratingReasons;
    private Set         mediaItems; // MediaItem
    
    protected MediaContent()
    { }
    public MediaContent(String title, Studio studio, Date releaseDate,
            String rating, String reasons) {
        this.title = title;
        this.studio = studio;
        this.releaseDate = releaseDate;
        this.rating = rating;
        ratingReasons = reasons;
        mediaItems = new HashSet();
    }
    public String getTitle() {
        return title;
    }
    public Studio getStudio() {
        return studio;
    }
    public Date getReleaseDate() {
        return releaseDate;
    }
    public String getRating() {
        return rating;
    }
    public String getRatingReasons() {
        return ratingReasons;
    }
    public abstract boolean validRating(String rating);
    public Set getMediaItems() {
        return Collections.unmodifiableSet(mediaItems);
    }
    public void addMediaItem(MediaItem item) {
        mediaItems.add(item);
    }
    public String getDescription() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(title);
        buffer.append(", ");
        buffer.append(studio.getName());
        buffer.append(", release date: ");
        buffer.append(formatReleaseDate());
        buffer.append(", rating: ");
        buffer.append(rating);
        buffer.append(", reasons for rating: ");
        buffer.append(ratingReasons);
        return buffer.toString();
    }
    public static Date parseReleaseDate(String val) {
        Date date = null;
        try {
            date = yearFmt.parse(val);
        } catch (java.text.ParseException exc) { }
        return date;
    }
    public String formatReleaseDate() {
        return yearFmt.format(releaseDate);
    }
}