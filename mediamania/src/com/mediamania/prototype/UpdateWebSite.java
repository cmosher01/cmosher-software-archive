package com.mediamania.prototype;

import com.mediamania.MediaManiaApp;

public class UpdateWebSite extends MediaManiaApp {
    private String  movieTitle;
    private String  newWebSite;

    public static void main (String[] args)
    {
        String title = args[0];
        String website = args[1];
        UpdateWebSite update = new UpdateWebSite(title, website);
        update.executeTransaction();
    }

    public UpdateWebSite(String title, String site)
    {
        movieTitle = title;
        newWebSite = site;
    }

    public void execute()
    {
        Movie movie = PrototypeQueries.getMovie(pm, movieTitle);
        if( movie == null ){
            System.err.print("Could not access movie with title of ");
            System.err.println(movieTitle);
            return;
        }
        movie.setWebSite(newWebSite);
    }
}
