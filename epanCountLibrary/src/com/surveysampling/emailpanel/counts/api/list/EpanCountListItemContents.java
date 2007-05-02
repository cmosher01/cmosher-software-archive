/*
 * Created on April 19, 2005
 */
package com.surveysampling.emailpanel.counts.api.list;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Contains skeletal information about a request
 * (generally useful for displaying a list of requests).
 * Objects of this class are immutable.
 * 
 * @author Chris Mosher
 */
public class EpanCountListItemContents
{
    private final String client;
    private final String topic;
    private final Date dateCreated;
    private final int cQuery;
    private final int cQuerySoFar;

    private transient final String string;
    private transient final int hashCode;



    /**
     * @param client
     * @param topic
     * @param dateCreated
     * @param cQuery
     * @param cQuerySoFar
     */
    public EpanCountListItemContents(final String client, final String topic, final Date dateCreated, final int cQuery, final int cQuerySoFar)
    {
        this.client = client;
        this.topic = topic;
        this.dateCreated = new Date(dateCreated.getTime());
        this.cQuery = cQuery;
        this.cQuerySoFar = cQuerySoFar;
        this.string = getString();
        this.hashCode = getHashCode();
    }



    /**
     * @return client name
     */
    public String getClient()
    {
        return this.client;
    }
    /**
     * @return topic
     */
    public String getTopic()
    {
        return this.topic;
    }
    /**
     * @return date record was created
     */
    public Date getDateCreated()
    {
        return new Date(this.dateCreated.getTime());
    }
    /**
     * @return number of queries
     */
    public int getQueryCount()
    {
        return this.cQuery;
    }
    /**
     * Only includes successfully completed queries.
     * @return queries done so far
     */
    public int getQueryCountSoFar()
    {
        return this.cQuerySoFar;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return this.string;
    }

    /**
     * Returns <code>true</code> if the given object is
     * an <code>EpanCountListItemContents</code> and has the same
     * client, topic, dateCreated, query count, and query-so-far count.
     * @param object
     * @return <code>true</code> if this is equal in content
     * to <code>object</code>
     */
    public boolean equals(final Object object)
    {
        if (!(object instanceof EpanCountListItemContents))
        {
            return false;
        }
        final EpanCountListItemContents that = (EpanCountListItemContents)object;
        if (!this.client.equals(that.client))
        {
            return false;
        }
        if (!this.topic.equals(that.topic))
        {
            return false;
        }
        if (!this.dateCreated.equals(that.dateCreated))
        {
            return false;
        }
        if (this.cQuery != that.cQuery)
        {
            return false;
        }
        if (this.cQuerySoFar != that.cQuerySoFar)
        {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code for the contents.
     * @return hash code
     */
    public int hashCode()
    {
        return this.hashCode;
    }





    private int getHashCode()
    {
        int hash = 17;

        hash *= 37;
        hash += this.client.hashCode();
        hash *= 37;
        hash += this.topic.hashCode();
        hash *= 37;
        hash += this.dateCreated.hashCode();
        hash *= 37;
        hash += this.cQuery;
        hash *= 37;
        hash += this.cQuerySoFar;

        return hash;
    }

    private static final Format fmtMonthDay = new SimpleDateFormat("yyyy-MM-dd");

    private String getString()
    {
        final StringBuffer s = new StringBuffer(64);



        // client
        if (this.client.length() > 0)
        {
            s.append(this.client);
        }
        else
        {
            s.append("[untitled]");
        }

        // topic
        if (this.topic.length() > 0)
        {
            s.append(": ");
            s.append(this.topic);
        }

        // date created
        s.append(" [");
        s.append(fmtMonthDay.format(this.dateCreated));
        s.append("] ");

        // query counts
        if (this.cQuerySoFar > 0 || this.cQuery > 0)
        {
            s.append(" (");
            if (this.cQuerySoFar != this.cQuery)
            {
                s.append(this.cQuerySoFar);
                s.append("/");
            }
            s.append(this.cQuery);
            s.append(")");
        }



        return s.toString();
    }
}
