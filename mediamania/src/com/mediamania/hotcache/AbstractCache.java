package com.mediamania.hotcache;

import java.util.HashMap;
import java.util.Map;

import com.mediamania.MediaManiaApp;
import com.mediamania.prototype.Movie;
import com.mediamania.prototype.PrototypeQueries;

public abstract class AbstractCache extends MediaManiaApp 
    implements com.mediamania.hotcache.CacheAccess {
    
    protected Map cache = new HashMap();

    /** Creates a new instance of AbstractCache.  The AbstractCache is the
     * base class for MasterCache and SlaveCache.
     */
    protected AbstractCache() {
    }

    /** Get the Movie by title.  If the movie is not in the cache, put it in.
     * @param title the title of the movie
     * @return the movie instance
     */
    public Movie getMovieByTitle(String title) {
        Movie movie = (Movie) cache.get(title);
        if (movie == null) {
            movie = PrototypeQueries.getMovie (pm, title);
            if (movie != null) {
                cache.put (title, movie);
            }
        }
        return movie;
    }
}