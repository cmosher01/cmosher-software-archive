package com.mediamania.hotcache;

import com.mediamania.prototype.Movie;

/** Manage a cache of persistent Movie instances.
 */
public interface CacheAccess {
    
    /** Get the Movie by title.
     * @param title the title of the movie
     * @return the movie instance
     */    
    Movie getMovieByTitle (String title);
    
    /** Update the Movie website.
     * @param title the title of the movie
     * @param website the new website for the movie
     */    
    void updateWebSite (String title, String website);
}