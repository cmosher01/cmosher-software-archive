/*
 * Created on Jun 15, 2005
 */
package com.surveysampling.emailpanel.counts.api.internal.sql;

/**
 * Oracle error codes.
 * 
 * @author Chris Mosher
 */
public final class OracleError
{
    /**
     * ORA-01013: user requested cancel of current operation
     */
    public static final int USER_REQUESTED_CANCEL = 1013;

    /**
     * ORA-08103: object no longer exists
     */
    public static final int OBJECT_WENT_AWAY = 8103;

    /**
     * ORA-00942: table or view does not exist
     */
    public static final int TABLE_NOT_FOUND = 942;



    private OracleError()
    {
        assert false;
    }
}
