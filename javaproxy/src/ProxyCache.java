/**
 * @(#)ProxyCache.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE SUITABILITY
 * OF THIS SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY
 * DAMAGES THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 * Author : Steve Yeong-Ching Hsueh
 */

import java.util.Date;



/**
 * ProxyCache stores the server response of an url
 */
public class ProxyCache
{
    String url = "";

    Date expiration;

    byte header[] = null;

    byte content[] = null;



    /**
     * constructor
     */
    ProxyCache()
    {
    }

    /**
     * test if this cache is expired
     */
    public boolean isExpired(Date d)
    {
        return expiration.after(d);
    }

    /**
     * set expiration date/time
     */
    public void setExpiration(Date d)
    {
        expiration = d;
    }

    /**
     * set expiration date/time
     */
    public void setExpiration(long d)
    {
        expiration = new Date(d);
    }

    /**
     * get expiration date/time
     */
    public Date getExpiration()
    {
        return expiration;
    }

    /**
     * set url
     */
    public void setURL(String newurl)
    {
        url = newurl;
    }

    /**
     * set server response header
     */
    public void setHeader(String h)
    {
        header = h.getBytes();
    }

    /**
     * set server response header
     */
    public void setHeader(byte h[])
    {
        header = h;
    }

    /**
     * set server response content
     */
    public void setContent(String c)
    {
        content = c.getBytes();
    }

    /**
     * set server response content
     */
    public void setContent(byte c[])
    {
        content = c;
    }

    /**
     * get server response header
     */
    public byte[] getHeader()
    {
        return header;
    }

    /**
     * get server response content
     */
    public byte[] getContent()
    {
        return content;
    }

    /**
     * test if the url match this cached url
     */
    public boolean matchURL(String target)
    {
        return url.equals(target);
    }

}