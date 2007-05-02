/*
 * Created on April 20, 2005
 */
package com.surveysampling.emailpanel.counts.api.list.monitor;

import com.surveysampling.util.key.DatalessKey;

/**
 * A default implementation of <code>MonitorableItem</code> that
 * uses a <code>DatalessKey</code> as a primary key to identify the
 * item, and keeps track of the serial number of the last change. This item
 * also holds a reference to the contents, as a generic </code>Object</code>.
 * Objects of the class are immutable, but only to the extent that
 * the underlying content object is immutable.
 * 
 * @author Chris Mosher
 */
public class MonitorableItemDefault implements MonitorableItem
{
    private final DatalessKey pk;
    private final ChangeSerialNumber lastChangeSerialNumber;
    private final Object contents;

    

    /**
     * Initializes this item.
     * @param pk primary key of the item; cannot be <code>null</code>.
     * @param lastMod serial number of latest change to item; cannot be <code>null</code>.
     * @param contents item contents; <code>null</code> indicates that the item has been deleted.
     */
    public MonitorableItemDefault(final DatalessKey pk, final ChangeSerialNumber lastMod, final Object contents)
    {
        this.pk = pk;
        this.lastChangeSerialNumber = lastMod;

        if (contents == null)
        {
            this.contents = new Deleted();
        }
        else
        {
            this.contents = contents;
        }
        assert this.contents != null;

        if (this.pk == null || this.lastChangeSerialNumber == null)
        {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @return if this item is deleted
     */
    public boolean isDeleted()
    {
        return this.contents instanceof Deleted;
    }

    /**
     * @see com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItem#getLastChangeSerialNumber()
     */
    public ChangeSerialNumber getLastChangeSerialNumber()
    {
        return this.lastChangeSerialNumber;
    }

    /**
     * @return the primary key
     */
    public DatalessKey getPK()
    {
        return this.pk;
    }

    /**
     * @return the underlying contents of this item
     * @throws UnsupportedOperationException if the item is deleted
     */
    public Object getContents() throws UnsupportedOperationException
    {
        if (isDeleted())
        {
            throw new UnsupportedOperationException("item is deleted");
        }
        return this.contents;
    }

    /**
     * Two <code>MonitorableItemDefault</code> instances are considered equal
     * if (and only if) they have the same primary key. Note that the contents of
     * the items are not considered when determining equality of the keys.
     * @param object
     * @return <code>true</code> if <code>object</code> has the
     * same primary key as this object.
     */
    public boolean equals(final Object object)
    {
        if (!(object instanceof MonitorableItemDefault))
        {
            return false;
        }
        final MonitorableItemDefault that = (MonitorableItemDefault)object;
        return this.pk.equals(that.pk);
    }

    /**
     * Returns the primary key's hash code.
     * @see com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItem#hashCode()
     */
    public int hashCode()
    {
        return this.pk.hashCode();
    }

    /**
     * Returns the result of calling <code>toString</code>
     * on this item's underlying contents.
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return this.contents.toString();
    }



    /**
     * Indicates the underlying contents have been deleted.
     */
    private static final class Deleted
    {
        /**
         * @see java.lang.Object#toString()
         */
        public String toString() { return "[this item has been deleted]"; }
    }
}
