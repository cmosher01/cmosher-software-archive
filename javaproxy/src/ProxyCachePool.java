/**
 * @(#)ProxyCache.java	April 3, 1998
 *
 * I MAKE NO WARRANTIES ABOUT THE SUITABILITY OF THIS SOFTWARE, EITHER
 * EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY DAMAGES THIS
 * SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 *
 * Author : Steve Yeong-Ching Hsueh
 */

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

/**
 * ProxyCachePool stores the server response of an url
 */
public class ProxyCachePool {
    private Vector slot = new Vector();
    private int    size = 0;
    private long   ttl  = 0; // time to live before it expires
    private boolean cacheEnabled = false;

    /**
     * constructor
     */
    ProxyCachePool(int s, int t) {

        if(s <= 0 || t <= 0) {
            cacheEnabled = false;
            return;
        }
        else cacheEnabled = true;

        size = s;
        this.ttl = t * 3600 * 1000;  // t: time in hours, ttl: time in millisecond
    }

    /**
     * set proxy cache
     */
    public void setCache(String url, String header, String content) {
        this.setCache(url, header, (content==null)? null : content.getBytes());
    }

    /**
     * set proxy cache
     */
    public void setCache(String url, String header, byte [] content) {
        if(!cacheEnabled) return;
        if(url == null || url.equals("")) return;
        if(header == null || header.equals("")) return;

        ProxyCache pc = new ProxyCache();
        Date now = new Date();
        pc.setExpiration(new Date(now.getTime() + ttl ));
        pc.setURL(url);
        pc.setHeader(header);
        pc.setContent(content);
        this.setCache(pc);
    }

    /**
     * set proxy cache
     */
    public void setCache(ProxyCache npc) {

        if(!cacheEnabled) return;

        ProxyCache oldest_cache = null;
        ProxyCache pc = null;

        if(slot.size() < size) slot.addElement(npc);
        else {

            // find the oldest cache in the slot
            for (Enumeration e = slot.elements() ; e.hasMoreElements() ;) {
                pc = (ProxyCache)e.nextElement();
                if(oldest_cache == null) oldest_cache = pc ;
                else {
                    if(oldest_cache.getExpiration().after(pc.getExpiration())) {
                        oldest_cache = pc;
                    }
                }
            }

            // replace the oldest cache slot with pc
            int index = slot.indexOf(oldest_cache);
            if(index <= 0) index = 0;  // this should not happen
            slot.setElementAt(npc, index);
        }
    }

    /**
     * return cache that matches the specified url in the pool
     * return null if no match
     */
    public ProxyCache getCache(String url) {

        if(!cacheEnabled) return null;

        ProxyCache pc;
        for (Enumeration e = slot.elements() ; e.hasMoreElements() ;) {
            pc = (ProxyCache)e.nextElement();
            if(pc != null && pc.matchURL(url)) {
                return pc;
            }
        }
        return null;
    }

    /**
     * test if cache is enabled
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }


}