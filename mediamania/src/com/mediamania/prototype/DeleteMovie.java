package com.mediamania.prototype;

import java.util.Iterator;
import java.util.Set;

import com.mediamania.MediaManiaApp;

public class DeleteMovie extends MediaManiaApp {
    private String  movieTitle;

    public static void main (String[] args)
    {
        String title = args[0];
        DeleteMovie deleteMovie = new DeleteMovie(title);
        deleteMovie.executeTransaction();
    }

    public DeleteMovie(String title)
    {
        movieTitle = title;
    }

    public void execute()
    {
        Movie movie = PrototypeQueries.getMovie(pm, movieTitle);
        if( movie == null ){
            System.err.print("Could not access movie with title of ");
            System.err.println(movieTitle);
            return;
        }
        Set cast = movie.getCast();
        Iterator iter = cast.iterator();
        while( iter.hasNext() ){
            Role role = (Role) iter.next();
            Actor actor = role.getActor();
            actor.removeRole(role);
        }
        pm.deletePersistentAll(cast);
        pm.deletePersistent(movie);
    }
}