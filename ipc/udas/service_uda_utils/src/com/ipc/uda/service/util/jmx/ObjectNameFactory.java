/**
 * 
 */
package com.ipc.uda.service.util.jmx;



import java.util.Hashtable;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;



/**
 * Creates {@link ObjectName}s. Use the {@link ObjectNameFactory#create} method to create
 * an {@link ObjectName} for a given a domain, a name, and a type.
 */
public final class ObjectNameFactory
{
    private static final String NAME_ATTR = "name";
    private static final String TYPE_ATTR = "type";

    /**
     * Creates an {@link ObjectName} for a given a domain, a name, and a type.
     * @param domainName domain name
     * @param name name
     * @param type type of object
     * @return a new {@link ObjectName}
     * @throws MalformedObjectNameException The <code>domainName</code>,
     * <code>name</code>, or <code>type</code> contains an illegal character
     * or does not follow the rules for quoting.
     */
    public static ObjectName create(final String domainName, final String name, final String type) throws MalformedObjectNameException
    {
        final Hashtable<String, String> attributes = createAttributeTable(name,type);
        return new ObjectName(domainName,attributes);
    }

    /**
     * Creates a hashtable with the given namd and type as values in pre-defined keys.
     * @param name
     * @param type
     * @return new Hashtable
     */
    private static Hashtable<String, String> createAttributeTable(final String name, final String type)
    {
        final Hashtable<String, String> attributes = new Hashtable<String, String>(2,Float.POSITIVE_INFINITY);

        attributes.put(NAME_ATTR,name);
        attributes.put(TYPE_ATTR,type);

        return attributes;
    }
}
