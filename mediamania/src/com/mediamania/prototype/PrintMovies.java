package com.mediamania.prototype;

import java.util.Iterator;
import java.util.Set;

import javax.jdo.Extent;

import com.mediamania.MediaManiaApp;

public class PrintMovies extends MediaManiaApp {

    public static void main(String[] args)
    {
        PrintMovies movies = new PrintMovies();
        movies.executeTransaction();
    }

    public void execute()
    {
        Extent extent = pm.getExtent(Movie.class, true);
        Iterator iter = extent.iterator();
        while (iter.hasNext()){
            Movie movie = (Movie) iter.next();
            System.out.print(movie.getTitle());           System.out.print(";");
            System.out.print(movie.getRating());          System.out.print(";");
            System.out.print(movie.formatReleaseDate() ); System.out.print(";");
            System.out.print(movie.getRunningTime());     System.out.print(";");
            System.out.println(movie.getGenres());
            Set cast = movie.getCast();
            Iterator castIterator = cast.iterator();
            while (castIterator.hasNext()) {
                Role role = (Role) castIterator.next();
                System.out.print("\t");
                System.out.print(role.getName());
                System.out.print(", ");
                System.out.println(role.getActor().getName());
            }
        }
        extent.close(iter);
    }
}