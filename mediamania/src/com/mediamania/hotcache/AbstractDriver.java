package com.mediamania.hotcache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.StringTokenizer;

import com.mediamania.prototype.Movie;

public class AbstractDriver {
    protected BufferedReader requestReader;
    protected BufferedReader updateReader;
    protected CacheAccess cache;
    protected int timeoutMillis;
    protected AbstractDriver(String updateURL, String requestURL,
        String timeout) {
        updateReader = openReader(updateURL);
        requestReader = openReader(requestURL);
        timeoutMillis = Integer.parseInt(timeout);
    }
    
    protected BufferedReader openReader (String urlName) {
        try {
            URL url = new URL(urlName);
            InputStream is = url.openStream();
            Reader r = new InputStreamReader(is);
            return new BufferedReader(r);
        } catch (Exception ex) {
            return null;
        }
    }
        
    protected void serviceReaders() {
        boolean done = false;
        boolean lastTime = false;
        try {
            while (!done) {
                if (updateReader.ready()) {
                    handleUpdate();
                    done = false;
                    lastTime = false;
                } else if (requestReader.ready()) {
                    handleRequest();
                    done = false;
                    lastTime = false;
                } else {
                    try {
                        Thread.sleep (timeoutMillis);
                        if (lastTime) done = true;
                        lastTime = true;
                    } catch (InterruptedException ex) {
                        done = true;
                    }
                }
            }
        } catch (Exception ex) {
            return;
        }
    }
    
    protected void handleRequest() throws IOException {
        String request = requestReader.readLine();
        Movie movie = cache.getMovieByTitle(request);
        System.out.println("Movie: " + movie.getTitle());
    }
    
    protected void handleUpdate() throws IOException {
        String update = updateReader.readLine();
        StringTokenizer tokenizer = new StringTokenizer(update, ";");
        String movieName = tokenizer.nextToken();
        String webSite = tokenizer.nextToken();
        cache.updateWebSite (movieName, webSite);
    }
}