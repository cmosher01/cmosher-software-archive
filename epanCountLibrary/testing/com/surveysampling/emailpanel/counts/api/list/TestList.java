/*
 * Created on April 19, 2005
 */
package com.surveysampling.emailpanel.counts.api.list;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import com.surveysampling.emailpanel.counts.api.list.monitor.ChangeSerialNumber;
import com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItemDefault;
import com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableSet;
import com.surveysampling.emailpanel.counts.api.list.monitor.exception.MonitoringException;
import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
import com.surveysampling.util.key.DatalessKeyAccessFactory;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * A <code>MonitorableSet</code> of <code>EpanCountListItemContents</code>s.
 * Pass an instance of this class to <code>ChangeMonitor</code> to monitor
 * a list of epanCount requests in the database, that the given user
 * has opted to see, since a given number of days ago.
 * 
 * @author Chris Mosher
 */
public class TestList implements MonitorableSet
{
    private final DatalessKeyAccess keyAccess;
    private final PreparedStatement st;

    /**
     * @param db
     * @throws SQLException
     * @throws DatalessKeyAccessCreationException
     */
    public TestList(final Connection db) throws SQLException, DatalessKeyAccessCreationException
    {
        this.keyAccess = DatalessKeyAccessFactory.createDatalessKeyAccess("UUID");
        this.st = initStatement(db);
    }

    private PreparedStatement initStatement(final Connection db) throws SQLException
    {
        final String sQueryTest =
            "SELECT " + 
            "    testListID pk, " + 
            "    modSerial, " + 
            "    to_char(timeLastMod,\'yyyy/mm/dd hh24:mi:ss.ff9\') name " + 
            "FROM " + 
            "    TestList " + 
            "WHERE " + 
            "    timeLastMod >= ? " + 
            "UNION ALL " + 
            "SELECT " + 
            "    testlistID pk, " + 
            "    timeLastMod, " + 
            "    NULL name " + 
            "FROM " + 
            "    TestListDeleted " + 
            "WHERE " + 
            "    timeLastMod >= ?";

        return db.prepareStatement(sQueryTest);
    }

    /**
     * @throws SQLException
     */
    public void close() throws SQLException
    {
        this.st.close();
    }

    /**
     * @param lastChangeSerialNumber
     * @param setOfChangedItems
     * @throws MonitoringException
     */
    public void getChangesSince(final ChangeSerialNumber lastChangeSerialNumber, final Set setOfChangedItems) throws MonitoringException
    {
        ResultSet rs = null;
        try
        {
            System.out.println();
            System.out.println("    checking for changes greater than serial #"+lastChangeSerialNumber);
            st.setLong(1,lastChangeSerialNumber.asLong());
            st.setLong(2,lastChangeSerialNumber.asLong());

            rs = st.executeQuery();
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
            if (rs != null)
            {
                try
                {
                    rs.close();
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
        final DatalessKey pk = keyAccess.createKeyFromResultSet(rs,rs.findColumn("pk"));
        final ChangeSerialNumber lcsn = new ChangeSerialNumber(rs.getLong("modSerial"));

        final Object contents = createContentsFromResultSet(rs);

        return new MonitorableItemDefault(pk,lcsn,contents);
    }

    /**
     * @param rs
     * @return
     * @throws SQLException
     */
    private Object createContentsFromResultSet(final ResultSet rs) throws SQLException
    {
        /*
         * In real situations, we would get a non-nullable column
         * and check if it is null; if so, the record is deleted,
         * which we indicate by returning null.
         */
        final String name = rs.getString("name"); // null means deleted
        if (rs.wasNull())
        {
            return null;
        }

        /*
         * If the record is known not to be deleted, then
         * get each remaining column, and build an ItemContents
         * object (which would normally be just a simple structure).
         */
        return new TestListItemContents(name);
    }
}
