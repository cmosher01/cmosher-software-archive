package com.mediamania.prototype;

import java.util.Collection;
import java.util.Iterator;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class PrototypeQueries {
    public static Actor getActor(PersistenceManager pm, String actorName)
    {
        Extent actorExtent = pm.getExtent(Actor.class, true);
        Query query = pm.newQuery(actorExtent, "name == actorName");
        query.declareParameters("String actorName");
        Collection result = (Collection) query.execute(actorName);
        Iterator iter = result.iterator();
        Actor actor = null;
        if (iter.hasNext()) actor = (Actor)iter.next();
        query.close(result);
        return actor;
    }
    public static Movie getMovie(PersistenceManager pm, String movieTitle)
    {
        Extent movieExtent = pm.getExtent(Movie.class, true);
        Query query = pm.newQuery(movieExtent, "title == movieTitle");
        query.declareParameters("String movieTitle");
        Collection result = (Collection) query.execute(movieTitle);
        Iterator iter = result.iterator();
        Movie movie = null;
        if (iter.hasNext()) movie = (Movie)iter.next();
        query.close(result);
        return movie;
    }
}