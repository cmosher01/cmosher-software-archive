package com.mediamania.content;

import java.util.Collection;
import java.util.Iterator;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class ContentQueries {
    public static Studio getStudioByName(PersistenceManager pm,
                                         String studioName) {
        Extent studioExtent = pm.getExtent(com.mediamania.content.Studio.class,
                                           false);
        Query query = pm.newQuery(studioExtent, "name == studioName");
        query.declareParameters("String studioName");
        Collection result = (Collection) query.execute(studioName);
        Iterator iter = result.iterator();
        Studio studio = (Studio) (iter.hasNext() ? iter.next() : null);
        query.close(result);
        return studio;
    }
    public static MediaPerson getMediaPerson(PersistenceManager pm,
                                             String person) {
        Extent personExtent = pm.getExtent(
                       com.mediamania.content.MediaPerson.class, false);
        Query query = pm.newQuery(personExtent, "mediaName == person");
        query.declareParameters("String person");
        Collection result = (Collection) query.execute(person);
        Iterator iter = result.iterator();
        MediaPerson mediaPerson =
            (MediaPerson) (iter.hasNext() ? iter.next() : null);
        query.close(result);
        return mediaPerson;
    }
}