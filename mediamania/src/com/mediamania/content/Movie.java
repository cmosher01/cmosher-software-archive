package com.mediamania.content;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Movie extends MediaContent {
    private static  String[]    allRatings = {"G","PG","PG-13","R","NC-17"};
    private         String      genres;
    private         Set         cast; // Role
    private         MediaPerson director;
    private         int         runningTime;
    private         String      webSite;

    private Movie()
    { }
    public Movie(String title, Studio studio, Date releaseDate,
            String rating, String reasons, String genres, int runningTime,
            MediaPerson director) { 
        super(title, studio, releaseDate, rating, reasons);
        this.runningTime = runningTime;
        this.genres = genres;
        cast = new HashSet();
        this.director = director;
        if (director != null) director.addMoviesDirected(this);
    }
    public boolean validRating(String rating) {
        for (int i = 0; i < allRatings.length; ++i) {
            if (rating.equals(allRatings[i])) return true;
        }
        return false;
    }
    public MediaPerson getDirector()
    {
        return director;
    }
    public Set getCast() {
        return Collections.unmodifiableSet(cast);
    }
    public void addRole(Role r) {
        cast.add(r);
    }
    public void removeRole(Role r) {
        cast.remove(r);
    }
    public String getDescription() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Movie: ");
        buffer.append(super.getDescription());
        buffer.append(", genre: ");
        buffer.append(genres);
        buffer.append(" running time: ");
        buffer.append(runningTime);
        return buffer.toString();
    }
}