/*
 * Created on Jun 20, 2005
 */
package com.surveysampling.emailpanel.counts.api.list.users;

import com.surveysampling.util.key.DatalessKey;

/**
 * Represents a user of the epanCount system.
 * Holds the user's name, department, and whether
 * or not the user's count requests are to be
 * shown in the user interface (for the currently
 * logged in user of the program).
 * 
 * @author Chris Mosher
 */
public class User implements Comparable
{
    private final DatalessKey keyUser;
    private final String nameLast;
    private final String nameFirst;
    private final String dept;
    private boolean seen;

    /**
     * @param keyUser primary key
     * @param nameLast
     * @param nameFirst
     * @param dept
     * @param seen
     */
    public User(
        final DatalessKey keyUser,
        final String nameLast,
        final String nameFirst,
        final String dept,
        final boolean seen)
    {
        this.keyUser = keyUser;
        this.nameLast = nameLast;
        this.nameFirst = nameFirst;
        this.dept = dept;
        this.seen = seen;
    }

    /**
     * @return if this user's counts are to be shown
     */
    public boolean isSeen()
    {
        return seen;
    }

    /**
     * Sets whether or not this user's counts are to
     * be shown in the user interface.
     * @param seen <code>true</code> if this user's counts are to be shown
     */
    public void setSeen(boolean seen)
    {
        this.seen = seen;
    }

    /**
     * Gets the name of this user's department.
     * @return user's department
     */
    public String getDept()
    {
        return dept;
    }

    /**
     * Gets this user's primary key.
     * @return primary key
     */
    public DatalessKey getKeyUser()
    {
        return keyUser;
    }

    /**
     * Gets this user's first name.
     * @return first name
     */
    public String getNameFirst()
    {
        return nameFirst;
    }

    /**
     * Gets this user's last name.
     * @return last name
     */
    public String getNameLast()
    {
        return nameLast;
    }

    /**
     * Checks on primary key only.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object object)
    {
        if (!(object instanceof User))
        {
            return false;
        }
        final User that = (User)object;
        return this.keyUser.equals(that.keyUser);
    }

    /**
     * Gets hash of primary key.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return this.keyUser.hashCode();
    }

    /**
     * Sorts by last name, first name.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final Object object)
    {
        final User that = (User)object;
        int c = 0;
        if (c == 0)
        {
            c = this.nameLast.compareToIgnoreCase(that.nameLast);
        }
        if (c == 0)
        {
            c = this.nameFirst.compareToIgnoreCase(that.nameFirst);
        }
        return c;
    }
}
