/*
 * Created on Apr 21, 2004
 */
package com.surveysampling.util;

import java.util.Map;

/**
 * Represents a cache of objects.
 */
public interface Cache
{
    boolean containsKey(Object key);
    Object get(Object key) throws KeyNotFoundException;
    void put(Object key, Object item);
    void remove(Object key) throws KeyNotFoundException;
    void clear();
    void getAll(Map map);
}
