package com.mediamania.hotcache;

import java.util.HashMap;
import java.util.Map;

import com.mediamania.prototype.Movie;

public class SlaveCache extends AbstractCache 
    implements com.mediamania.hotcache.CacheAccess {
    
    /** Creates a new instance of SlaveCache.  The SlaveCache performs 
     * lookups of the database and manages a cache of Movie.
     */
    public SlaveCache() {
    }

    /** Update the Movie website in the cache, only if it is already there.  
     * The datastore will be updated by the MasterCache.
     * @param title the title of the movie
     * @param website the new website for the movie
     */
    public void updateWebSite(String title, String website) {
        Movie movie = (Movie)cache.get (title);
        if (movie == null) 
            return;
        movie.setWebSite (website);
    }
    
    public void execute() {
    }
    
    protected static Map getPropertyOverrides()
    {
        Map overrides = new HashMap();
        overrides.put ("javax.jdo.options.NontransactionalRead", "true");
        overrides.put ("javax.jdo.options.NontransactionalWrite", "true");
        return overrides;
    }
}