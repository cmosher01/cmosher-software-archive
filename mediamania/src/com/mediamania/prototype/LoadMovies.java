package com.mediamania.prototype;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.util.StringTokenizer;

import com.mediamania.MediaManiaApp;

public class LoadMovies extends MediaManiaApp {
    private BufferedReader  reader;

    public static void main(String[] args)
    {
        LoadMovies loadMovies = new LoadMovies(args[0]);
        loadMovies.executeTransaction();
    }

    public LoadMovies(String filename)
    {
        try {
            FileReader fr = new FileReader(filename);
            reader = new BufferedReader(fr);
        } catch(Exception e){
            System.err.print("Unable to open input file ");
            System.err.println(filename);
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }

    public void execute()
    {
        try {
            while( reader.ready() ){
                String line = reader.readLine();
                parseMovieData(line);
            }
        } catch(java.io.IOException e){
            System.err.println("Exception reading input file");
            System.err.println(e);
        }
    }

    public void parseMovieData(String line)
    {
        StringTokenizer tokenizer = new StringTokenizer(line, ";");
        String title = tokenizer.nextToken();
        String dateStr = tokenizer.nextToken();
        Date releaseDate = Movie.parseReleaseDate(dateStr);
        int runningTime = 0;
        try {
            runningTime = Integer.parseInt(tokenizer.nextToken());
        } catch(java.lang.NumberFormatException e){
            System.err.print("Exception parsing running time for ");
            System.err.println(title);
        }
        String rating = tokenizer.nextToken();
        String genres = tokenizer.nextToken();
        Movie movie = new Movie(title, releaseDate, runningTime, rating,genres);
        pm.makePersistent(movie);
    }
}