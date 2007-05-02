/*
 * Created on April 19, 2005
 */
package com.surveysampling.emailpanel.counts.api.list;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Set;

import com.surveysampling.emailpanel.counts.api.internal.sql.SQLTimestampAccess;
import com.surveysampling.emailpanel.counts.api.list.monitor.ChangeSerialNumber;
import com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItemDefault;
import com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableSet;
import com.surveysampling.emailpanel.counts.api.list.monitor.exception.MonitoringException;
import com.surveysampling.sql.DatabaseUtil;
import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;

/**
 * A <code>MonitorableSet</code> of <code>EpanCountListItemContents</code>s.
 * Pass an instance of this class to <code>ChangeMonitor</code> to monitor
 * a list of epanCount requests in the database, that the given user
 * has opted to see, since a given number of days ago.
 * 
 * @author Chris Mosher
 */
public class EpanCountList implements MonitorableSet
{
    private final Connection db;
    private final DatalessKeyAccess keyAccess;

    /**
     * @param db
     * @param keyAccess
     */
    public EpanCountList(final Connection db, final DatalessKeyAccess keyAccess)
    {
        this.db = db;
        this.keyAccess = keyAccess;
    }

    /**
     * @param lastChangeSerialNumber
     * @param setOfChangedItems
     * @throws MonitoringException
     */
    public void getChangesSince(final ChangeSerialNumber lastChangeSerialNumber, final Set setOfChangedItems) throws MonitoringException
    {
        synchronized (this.db)
        {
            PreparedStatement ps = null;
            try
            {
                ps = this.db.prepareStatement(
                    "SELECT * FROM EpanCountList WHERE modSerial > ? ORDER BY modSerial DESC");
                ps.setLong(1,lastChangeSerialNumber.asLong());

                final ResultSet rs = ps.executeQuery();
                while (rs.next())
                {
                    final MonitorableItemDefault item = createItemFromResultSet(rs);
                    setOfChangedItems.add(item);
                }
            }
            catch (final SQLException e)
            {
                throw new MonitoringException(e);
            }
            finally
            {
                DatabaseUtil.closeStatementNoThrow(ps);
                try
                {
                    this.db.commit();
                }
                catch (final Throwable e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This should work for most real situations, too.
     * @param rs
     * @return
     * @throws SQLException
     */
    private MonitorableItemDefault createItemFromResultSet(final ResultSet rs) throws SQLException
    {
        /*
         * We always need a primary key and date last modified; that way
         * we can use MonitorableItemDefault.
         */
        final DatalessKey pk = this.keyAccess.createKeyFromResultSet(rs,rs.findColumn("pk"));
        final ChangeSerialNumber lcsn = new ChangeSerialNumber(rs.getLong("modSerial"));

        final Object contents = createContentsFromResultSet(rs);

        return new MonitorableItemDefault(pk,lcsn,contents);
    }

    /**
     * @param rs
     * @return
     * @throws SQLException
     */
    private static Object createContentsFromResultSet(final ResultSet rs) throws SQLException
    {
        /*
         * Get a non-nullable column
         * and check if it is null; if so, the record is deleted,
         * which we indicate by returning null.
         */
        final String topic = rs.getString("topic");// null means deleted
        if (rs.wasNull())
        {
            return null;
        }

        /*
         * If the record is known not to be deleted, then
         * get each remaining column, and build an ItemContents
         * object (which is just a simple structure).
         */
        final String client = rs.getString("clientName");
        final Date dateCreated = SQLTimestampAccess.getTimestampFromResultSet(rs,rs.findColumn("timeCreated"));
        final int cQuery = rs.getInt("cQuery");
        final int cQuerySoFar = rs.getInt("cQuerySoFar");
        

        return new EpanCountListItemContents(client,topic,dateCreated,cQuery,cQuerySoFar);
    }
}
