/*
 * Created on May 3, 2006
 */
package testmaps;

import java.util.Random;

public class MyObj
{
    private static final Random r = new Random();
    private final int n;
    private int hash;

    public MyObj(final int n)
    {
        this.n = n;
        this.hash = n*37;//r.nextInt();
    }

    public int hashCode()
    {
        return this.hash;
    }

    public String toString()
    {
        return Integer.toHexString(this.n);
    }
}
