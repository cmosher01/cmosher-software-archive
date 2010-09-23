package com.ipc.uda.service.util;

import java.util.Collection;
import java.util.StringTokenizer;

/**
 * This class contains static methods that operate on generic {@link String}s.
 * 
 * @author mosherc
 */
public final class Strings
{
    Strings()
    {
        throw new IllegalStateException();
    }

    /**
     * Splits a tokenized list, delimited with commas, semi-colons, spaces,
     * tabs, carriage-returns, and/or line-feeds, and adds the items to the
     * given {@link Collection}.
     * @param list the tokenized string to be split into individual items
     * @param rItem the {@link Collection} to add the items to
     */
    public static void splitList(final String list, final Collection<String> rItem)
    {
        final StringTokenizer strtok = new StringTokenizer(list,",; \t\n\r");
        while (strtok.hasMoreTokens())
        {
            rItem.add(strtok.nextToken());
        }
    }
}
