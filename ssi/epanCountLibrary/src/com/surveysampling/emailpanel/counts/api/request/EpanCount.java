/*
 * Created on April 26, 2005
 */
package com.surveysampling.emailpanel.counts.api.request;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.surveysampling.emailpanel.counts.api.internal.sql.SQLTimestampAccess;
import com.surveysampling.sql.DatabaseUtil;
import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccessFactory;



/**
 * Holds information about each actual count query in
 * a request.
 * 
 * @author Chris Mosher
 */
public class EpanCount
{
    private final String name;
    private final int indexInRequest;

    private final boolean existsCountResult;
    private final long countResult;
    private final boolean existsErrorMsg;
    private final String errorMsg;

    private final Date timeAsOf;



    /**
     * @param name
     * @param indexInRequest
     */
    public EpanCount(final String name, final int indexInRequest)
    {
        if (name == null)
        {
            this.name = "";
        }
        else
        {
            this.name = name;
        }
        this.indexInRequest = indexInRequest;
        this.existsCountResult = false;
        this.countResult = 0;
        this.existsErrorMsg = false;
        this.errorMsg = null;
        this.timeAsOf = null;
    }

    /**
     * @param rs
     * @throws SQLException
     */
    public EpanCount(final ResultSet rs) throws SQLException
    {
        final String colName = rs.getString("name");
        if (colName == null)
        {
            this.name = "";
        }
        else
        {
            this.name = colName;
        }
        this.indexInRequest = rs.getInt("indexInRequest");

        this.countResult = rs.getLong("countResult");
        final boolean countResultIsNull = rs.wasNull();
        this.existsCountResult = !countResultIsNull;

        this.errorMsg = rs.getString("errorMsg");
        final boolean errorMsgIsNull = rs.wasNull();
        this.existsErrorMsg = !errorMsgIsNull;

        this.timeAsOf = SQLTimestampAccess.getTimestampFromResultSet(rs, rs.findColumn("timeAsOf"));
    }

    /**
     * Index within parent <code>EpanCountRequest</code> object
     * of this <code>EpanCount</code> child record.
     * @return the index (0-based)
     */
    public int getIndex()
    {
        return this.indexInRequest;
    }

    /**
     * @return user-readable name for this count
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Finished running (successfully or with error).
     * Equivalent to completedWithError()||completedSuccessfully().
     * @return if done
     */
    public boolean isDone()
    {
        return this.existsCountResult || this.existsErrorMsg;
    }

    /**
     * @return if finished with error (so count is not available)
     */
    public boolean completedWithError()
    {
        return this.existsErrorMsg;
    }

    /**
     * @return if finished with success
     */
    public boolean completedSuccessfully()
    {
        return this.existsCountResult;
    }

    /**
     * @return the count
     * @throws IllegalStateException if the count didn't finish successfully (yet)
     */
    public long getCount() throws IllegalStateException
    {
        if (!this.existsCountResult)
        {
            throw new IllegalStateException("Resulting count for CountQuery does not exist.");
        }
        return this.countResult;
    }

    /**
     * @return
     * @throws IllegalStateException
     */
    public String getErrorMessage() throws IllegalStateException
    {
        if (!this.existsErrorMsg)
        {
            throw new IllegalStateException("Error message for CountQuery does not exist.");
        }
        return this.errorMsg;
    }

    /**
     * As of date for data against which this query
     * was run, or null if the query has been run yet.
     * @return as of date, or null
     */
    public Date getTimeAsOf()
    {
        if (this.timeAsOf == null)
        {
            return null;
        }
        return new Date(this.timeAsOf.getTime());
    }



    void insert(final Connection db, final DatalessKey keyCountRequestParent) throws ClassCastException, SQLException
    {
        PreparedStatement ps = null;
        try
        {
            ps = db.prepareStatement(
                "INSERT INTO CountQuery (countRequestID,indexInRequest,name) VALUES (?,?,?)");
    
            DatalessKeyAccessFactory.getDatalessKeyAccess(keyCountRequestParent).putKeyToPreparedStatement(keyCountRequestParent,ps,1);
            ps.setInt(2,this.indexInRequest);
            ps.setString(3,this.name);
    
            final int cUpdatedRows = ps.executeUpdate();
            if (cUpdatedRows != 1)
            {
                throw new SQLException("CountQuery row could not be inserted");
            }
        }
        finally
        {
            DatabaseUtil.closeStatementNoThrow(ps);
        }
    }
}
