package com.mediamania.hotcache;

import java.util.HashMap;
import java.util.Map;

import com.mediamania.prototype.Movie;

public class MasterCache extends AbstractCache 
    implements com.mediamania.hotcache.CacheAccess {
    
    /** Creates a new instance of MasterCache.  The MasterCache performs 
     * updates of the database and manages a cache of Movie.
     */
    public MasterCache() {
    }
    
    /** Update the Movie website.
     * @param title the title of the movie
     * @param website the new website for the movie
     */
    public void updateWebSite(String title, String website) {
        Movie movie = getMovieByTitle (title);
        if (movie != null) {
            tx.begin();
            movie.setWebSite (website);
            tx.commit();
        }
    }
    
    public void execute() {
    }
    
    protected static Map getPropertyOverrides()
    {
        Map overrides = new HashMap();
        overrides.put ("javax.jdo.options.NontransactionalRead", "true");
        overrides.put ("javax.jdo.options.RetainValues", "true");
        return overrides;
    }
}