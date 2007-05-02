/*
 * Created on Jul 29, 2005
 *
 */
package com.surveysampling.emailpanel.counts.data;

import java.util.HashMap;

import com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItemDefault;
import com.surveysampling.util.key.DatalessKey;

/**
 * A Folder will contain all the count requests. Folders
 * can contain requests made today, recently, or old counts.
 * 
 * @author james
 *
 */
public abstract class Folder
{
	HashMap hm = new HashMap();
    
    /**
     * Add an item to the folder.
     * 
     * @param item
     */
	public void add(MonitorableItemDefault item)
    {
        hm.put(item.getPK(),item);
    }
    
	/**
	 * Remove the item from the folder.
	 * 
	 * @param item
	 */
    public void remove(MonitorableItemDefault item)
    {
        hm.remove(item.getPK());
    }
    /**
     * 
     * @param key
     * @return	the item that pertains to this key
     */
    public MonitorableItemDefault getCountRequest(DatalessKey key)
    {
        return (MonitorableItemDefault) hm.get(key);
    }
    
    /**
     * 
     * @return how many requests are in here
     */
    public int getLength()
    {
        return hm.size();
    }
    
    /**
     * Need to provide the name of the folder.
     */
    public abstract String toString();

}
