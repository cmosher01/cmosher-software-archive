package com.mediamania.content;

import java.util.Date;

public class Game extends MediaContent {
    private static  String[]    allRatings = {"EC","K-A","E","T","M","AO","RP"};
    
    public Game() {
    }
    public Game(String title, Studio studio, Date releaseDate,
            String rating, String reasons) {
        super(title, studio, releaseDate, rating, reasons);
    }
    
    public boolean validRating(String rating) {
        for (int i = 0; i < allRatings.length; ++i) {
            if (rating.equals(allRatings[i])) return true;
        }
        return false;
    }
}