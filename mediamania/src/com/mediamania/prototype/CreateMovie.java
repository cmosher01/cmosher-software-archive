package com.mediamania.prototype;

import java.util.Calendar;
import java.util.Date;

import com.mediamania.MediaManiaApp;

public class CreateMovie extends MediaManiaApp {

    public static void main(String[] args)
    {
        CreateMovie createMovie = new CreateMovie();
        createMovie.executeTransaction();
    }

    public void execute()
    {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, 1997);
        Date date = cal.getTime();

        Movie movie = new Movie("Titanic", date, 194, "PG-13",
                                "historical, drama");
        pm.makePersistent(movie);
    }
}