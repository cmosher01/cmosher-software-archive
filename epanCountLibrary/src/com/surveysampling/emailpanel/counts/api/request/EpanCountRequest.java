/*
 * Created on April 26, 2005
 */
package com.surveysampling.emailpanel.counts.api.request;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.surveysampling.emailpanel.counts.api.criteria.BreakOutType;
import com.surveysampling.emailpanel.counts.api.criteria.EpanCountCriteria;
import com.surveysampling.emailpanel.counts.api.criteria.EpanCountDisplayNameBuilder;
import com.surveysampling.emailpanel.counts.api.criteria.GenderEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.GeographyType;
import com.surveysampling.emailpanel.counts.api.criteria.ObjectFactory;
import com.surveysampling.emailpanel.counts.api.internal.sql.EpanCountCriteriaAccess;
import com.surveysampling.emailpanel.counts.api.internal.sql.OracleClobAccess;
import com.surveysampling.emailpanel.counts.api.internal.sql.OracleError;
import com.surveysampling.emailpanel.counts.api.internal.sql.SQLTimestampAccess;
import com.surveysampling.emailpanel.counts.api.list.monitor.ChangeSerialNumber;
import com.surveysampling.emailpanel.counts.api.request.exception.ConcurrentModificationException;
import com.surveysampling.emailpanel.counts.api.request.exception.CountAbortedByUser;
import com.surveysampling.emailpanel.counts.api.request.exception.SummaryTableUpdateInProgress;
import com.surveysampling.emailpanel.counts.api.request.internal.EpanCountSQLBuilder;
import com.surveysampling.emailpanel.counts.api.request.internal.NonContinentalGeoID;
import com.surveysampling.emailpanel.xdem.InvalidXdemCriteria;
import com.surveysampling.emailpanel.xdem.XdemCriteria;
import com.surveysampling.emailpanel.xdem.metadata.Metadata;
import com.surveysampling.sql.DatabaseUtil;
import com.surveysampling.sql.LookupException;
import com.surveysampling.sql.SQLHelpers;
import com.surveysampling.util.Flag;
import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
import com.surveysampling.util.key.DatalessKeyAccessFactory;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;



/**
 * Represents one request for (one or more) counts.
 * 
 * @author Chris Mosher
 */
public class EpanCountRequest implements Cloneable
{
    private final DatalessKeyAccess keyAccess;
    private final DatalessKeyAccess keyAccessGeo = DatalessKeyAccessFactory.createDatalessKeyAccess("Long");
    private final EpanCountDisplayNameBuilder factoryName;
    private final Connection dbRequest;
    private final Connection dbCount;
    private DatalessKey pk;

    private ChangeSerialNumber modSerial;

    private String createdBy;
    private Date dateCreated;
    private String lastModBy;
    private Date dateLastMod;

    private Date dateRequested;

    private String topic;
    private String clientName;
    private EpanCountCriteria criteria;
    private int xdemExtSpecID;
    private Metadata xdemMetadata;
    private XdemCriteria xdemCriteria;
    private NonContinentalGeoID nContGeoID;

    private final List<EpanCount> rEpanCount = new ArrayList<EpanCount>();

    private static final int K = 1024;

    private final Flag running = new Flag();
    private final Flag abort = new Flag();
    private PreparedStatement statementRunning;

    /**
     * Creates a new count request (in memory only, not in the database).
     * @param dbRequest
     * @param dbCount
     * @param keyAccess
     * @param factoryName
     * @param xdemMetadata
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     */
    public EpanCountRequest(final Connection dbRequest, final Connection dbCount, final DatalessKeyAccess keyAccess, final EpanCountDisplayNameBuilder factoryName, final Metadata xdemMetadata, final NonContinentalGeoID ncgid) throws JAXBException, DatalessKeyAccessCreationException
    {
        this.keyAccess = keyAccess;
        this.factoryName = factoryName;
        this.dbRequest = dbRequest;
        this.dbCount = dbCount;
        this.pk = keyAccess.createNewKey();

        this.modSerial = ChangeSerialNumber.getSerialNumberLowerLimit();

        this.dateCreated = new Date();
        this.createdBy = "";
        this.dateLastMod = new Date();
        this.lastModBy = "";

        this.topic = "";
        this.clientName = "";

        this.criteria = new ObjectFactory().createEpanCountCriteria();
        this.criteria.setBreakOut(new ObjectFactory().createBreakOutType());

        this.xdemMetadata = xdemMetadata;
        this.xdemCriteria = new XdemCriteria();
        this.xdemCriteria.setMetadata(this.xdemMetadata);

        this.nContGeoID = ncgid;
        this.rEpanCount.clear();
    }

    /**
     * Reads in an existing count from the database.
     * That includes all related <code>EpanCount</code> records.
     * @param pk the primary key of the EpanCountRequest record to read
     * @param dbRequest
     * @param dbCount
     * @param keyAccess
     * @param factoryName
     * @param xdemMetadata
     * @throws SQLException
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     */
    public EpanCountRequest(final DatalessKey pk, final Connection dbRequest, final Connection dbCount, final DatalessKeyAccess keyAccess, final EpanCountDisplayNameBuilder factoryName, final Metadata xdemMetadata, final NonContinentalGeoID ncgid) throws SQLException, JAXBException, DatalessKeyAccessCreationException
    {
        this.keyAccess = keyAccess;
        this.factoryName = factoryName;
        this.dbRequest = dbRequest;
        this.dbCount = dbCount;
        this.pk = pk;

        synchronized (this.dbRequest)
        {
            PreparedStatement ps = null;
            try
            {
                ps = this.dbRequest.prepareStatement(
                    "SELECT "
                        + "Request.modSerial, "
                        + "Request.timeCreated, "
                        + "Creator.lastName||\', \'||Creator.firstName createdBy, "
                        + "Request.timeLastMod, "
                        + "LastMod.lastName||\', \'||LastMod.firstName lastModBy, "
                        + "Request.topic, "
                        + "Request.clientName, "
                        + "Request.timeRequested, "
                        + "Request.criteriaXML.getClobVal() criteriaXML, "
                        + "Request.xdemExtSpecID "
                    + "FROM "
                        + "CountRequest Request "
                        + "LEFT OUTER JOIN SSIEmployee Creator ON Creator.ssiEmployeeID = Request.createdBy "
                        + "LEFT OUTER JOIN SSIEmployee LastMod ON LastMod.ssiEmployeeID = Request.lastModBy "
                    + "WHERE "
                        + "Request.countRequestID = ?");

                DatalessKeyAccessFactory.getDatalessKeyAccess(this.pk).putKeyToPreparedStatement(this.pk,ps,1);

                final ResultSet rs = ps.executeQuery();
                if (!rs.next())
                {
                    throw new SQLException("CountRequest row not found");
                }

                this.modSerial = new ChangeSerialNumber(rs.getLong("modSerial"));

                this.dateCreated = SQLTimestampAccess.getTimestampFromResultSet(rs,rs.findColumn("timeCreated"));
                this.createdBy = rs.getString("createdBy");
                this.dateLastMod = SQLTimestampAccess.getTimestampFromResultSet(rs,rs.findColumn("timeLastMod"));
                this.lastModBy = rs.getString("lastModBy");

                this.dateRequested = SQLTimestampAccess.getTimestampFromResultSet(rs,rs.findColumn("timeRequested"));

                this.topic = rs.getString("topic");
                this.clientName = rs.getString("clientName");

                this.criteria = EpanCountCriteriaAccess.getCriteriaFromResultSet(rs,rs.findColumn("criteriaXML"));

                this.xdemMetadata = xdemMetadata;
                this.xdemExtSpecID = rs.getInt("xdemExtSpecID");
            }
            finally
            {
                DatabaseUtil.closeStatementNoThrow(ps);
            }

            readChildCountRecords();

            readXdem();
            
            this.nContGeoID = ncgid;

            this.dbRequest.commit();
        }
    }

    /**
     * @throws SQLException
     */
    private void readChildCountRecords() throws SQLException
    {
        PreparedStatement ps = null;
        try
        {
            ps = this.dbRequest.prepareStatement(
                "SELECT "+
                    "name, "+
                    "indexInRequest, "+ 
                    "countResult, "+
                    "errorMsg, "+
                    "timeAsOf "+
                "FROM "+
                    "CountQuery "+ 
                "WHERE "+
                    "countRequestID = ? "+
                "ORDER BY indexInRequest");

            DatalessKeyAccessFactory.getDatalessKeyAccess(this.pk).putKeyToPreparedStatement(this.pk,ps,1);

            final ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                final EpanCount epanCount = new EpanCount(rs);
                this.rEpanCount.add(epanCount);
            }
        }
        finally
        {
            DatabaseUtil.closeStatementNoThrow(ps);
        }
    }

    private void readXdem() throws SQLException
    {
        this.xdemCriteria = new XdemCriteria();
        this.xdemCriteria.setMetadata(this.xdemMetadata);
        if (this.xdemExtSpecID > 0)
        {
            this.xdemCriteria.load(this.dbRequest,this.xdemExtSpecID);
            assert !this.xdemCriteria.isEmpty();
        }
        else
        {
            assert this.xdemCriteria.isEmpty();
        }
    }

    /**
     * @throws CloneNotSupportedException
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        try
        {
            final EpanCountRequest that = (EpanCountRequest)super.clone();

            fixupClone(that);

            return that;
        }
        catch (final Throwable e)
        {
            throw (CloneNotSupportedException)new CloneNotSupportedException().initCause(e);
        }
    }

    /**
     * @param that
     * @throws UnsupportedOperationException
     * @throws JAXBException
     */
    private void fixupClone(final EpanCountRequest that) throws UnsupportedOperationException, JAXBException
    {
        that.pk = this.keyAccess.createNewKey();
        that.modSerial = ChangeSerialNumber.getSerialNumberLowerLimit();

        that.dateCreated = new Date();
        that.createdBy = "";
        that.dateLastMod = new Date();
        that.lastModBy = "";

        that.dateRequested = null;

        that.criteria = EpanCountCriteriaAccess.cloneCriteria(this.criteria);

        that.xdemCriteria = (XdemCriteria)this.xdemCriteria.clone();
        that.xdemExtSpecID = 0;

        that.rEpanCount.clear();
    }

    /**
     * Checks if this <code>EpanCountRequest</code> has the
     * same primary key as the given <code>EpanCountRequest</code>.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object object)
    {
        if (!(object instanceof EpanCountRequest))
        {
            return false;
        }
        final EpanCountRequest that = (EpanCountRequest)object;
        return this.pk.equals(that.pk);
    }

    /**
     * Returns a hash code for this <code>EpanCountRequest</code>'s primary key.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return this.pk.hashCode();
    }

    /**
     * Gets this object's primary key.
     * @return the primary key
     */
    public DatalessKey getPK()
    {
        return this.pk;
    }

    /**
     * Returns the serial number of the (latest) change to
     * this item.
     * @return last modification serial number
     */
    public ChangeSerialNumber getLastChangeSerialNumber()
    {
        return this.modSerial;
    }

    /**
     * Gets this object's criteria.
     * @return criteria
     */
    public EpanCountCriteria getCriteria()
    {
        return this.criteria;
    }

    /**
     * Gets this object's XDem criteria.
     * @return criteria
     */
    public XdemCriteria getXdemCriteria()
    {
        return this.xdemCriteria;
    }

    /**
     * Sets this object's XDem criteria.
     * @param xdemCriteria new XDem criteria to set
     */
    public void setXdemCriteria(final XdemCriteria xdemCriteria)
    {
        if (xdemCriteria == null)
        {
            throw new IllegalArgumentException("cannot be null");
        }
        this.xdemCriteria = xdemCriteria;
        this.xdemCriteria.setMetadata(this.xdemMetadata);
    }

    /**
     * @return clientName
     */
    public String getClientName()
    {
        return this.clientName;
    }

    /**
     * @param clientName
     */
    public void setClientName(final String clientName)
    {
        this.clientName = clientName;
    }

    /**
     * @return topic
     */
    public String getTopic()
    {
        return this.topic;
    }

    /**
     * @param topic
     */
    public void setTopic(final String topic)
    {
        this.topic = topic;
    }

    /**
     * Gets the total number of <code>EpanCount</code> queries
     * in this <code>EpanCountRequest</code>.
     * @return count of <code>EpanCount</code> child records
     */
    public int getQueryCount()
    {
        return this.rEpanCount.size();
    }

    /**
     * Gets an iterator that iterates over <code>EpanCount</code>
     * child records.
     * @return iterator for <code>EpanCount</code> items
     */
    public Iterator /*<EpanCount>*/ iterator()
    {
        return this.rEpanCount.iterator();
    }

    /**
     * Gets an <code>EpanCount</code> child record,
     * given its index (0-based).
     * @param index of <code>EpanCount</code> item
     * @return <code>EpanCount</code> item
     */
    public EpanCount getCount(final int index)
    {
        return this.rEpanCount.get(index);
    }

    /**
     * Checks if this object is "frozen," meaning that
     * it has already started running.
     * @return <code>true</code> if this record is frozen
     */
    public boolean isFrozen()
    {
        // Frozen means not edit-able (that is, can't change criteria)
        // Basically, as soon as the user starts the request running,
        // it is frozen from then on.

        return this.dateRequested != null;
    }

    /**
     * @return date counts were requested to run, or null
     */
    public Date getDateRequested()
    {
        if (this.dateRequested == null)
        {
            return null;
        }
        return new Date(this.dateRequested.getTime());
    }

    /**
     * Checks if this request is currently running (a database query for a count).
     * @return <code>true</code> if currently running
     */
    public boolean isRunning()
    {
        return this.running.isTrue();
    }

    /**
     * @return Returns the createdBy.
     */
    public String getCreatedBy()
    {
        return this.createdBy;
    }
    /**
     * @return Returns the dateCreated.
     */
    public Date getDateCreated()
    {
        return this.dateCreated;
    }
    /**
     * @return Returns the dateLastMod.
     */
    public Date getDateLastMod()
    {
        return this.dateLastMod;
    }
    /**
     * @return Returns the lastModBy.
     */
    public String getLastModBy()
    {
        return this.lastModBy;
    }
    /**
     * Writes this request to permanent data store.
     * If the underlying record in the database was modified
     * (by another user) since this record was read, then
     * throws <code>ConcurrentModificationException</code>.
     * @return the request (read back in from the database after the save)
     * @throws ConcurrentModificationException
     * @throws LookupException
     * @throws ClassCastException
     * @throws SQLException
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     */
    public EpanCountRequest saveToDatabase() throws ClassCastException, ConcurrentModificationException, LookupException, SQLException, JAXBException, DatalessKeyAccessCreationException
    {
        synchronized (this.dbRequest)
        {
            save();
            this.dbRequest.commit();

            /*
             * Now re-read this request to get any new fields that
             * have been filled in by the database.
             */
            final EpanCountRequest newRequest = new EpanCountRequest(this.pk,this.dbRequest,this.dbCount,this.keyAccess,this.factoryName,this.xdemMetadata, this.nContGeoID);
            if (isFrozen())
            {
                newRequest.setFrozen(true);
            }
            return newRequest;
        }
    }

    private void save() throws ConcurrentModificationException, ClassCastException, JAXBException, SQLException, LookupException
    {
        Savepoint savepoint = null;
        try
        {
            savepoint = this.dbRequest.setSavepoint();

            if (this.modSerial.equals(ChangeSerialNumber.getSerialNumberLowerLimit()))
            {
                // this is a new record, so insert it
                tryInsert();
            }
            else
            {
                // this record already exists in the
                // database, so try to update it
                tryUpdate();
            }

            savepoint = null;
        }
        finally
        {
            if (savepoint != null)
            {
                try
                {
                    this.dbRequest.rollback(savepoint);
                }
                catch (final Throwable e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void tryInsert() throws ClassCastException, JAXBException, SQLException, LookupException
    {
        PreparedStatement ps = null;
        try
        {
            ps = this.dbRequest.prepareStatement(
                "INSERT INTO CountRequest(countRequestID,topic,clientName,criteriaXML,xdemExtSpecID) "+
                "VALUES (?,?,?,XMLTYPE(?),?)");

            DatalessKeyAccessFactory.getDatalessKeyAccess(this.pk).putKeyToPreparedStatement(this.pk,ps,1);
            ps.setString(2,this.topic);
            ps.setString(3,this.clientName);
            EpanCountCriteriaAccess.putCriteriaToPreparedStatement(this.criteria,ps,4);
            if (this.xdemCriteria.isEmpty())
            {
                ps.setNull(5,Types.NUMERIC);
            }
            else
            {
                this.xdemExtSpecID = SQLHelpers.getID(this.dbRequest,"ExtSpecID_Seq");
                ps.setInt(5,this.xdemExtSpecID);
            }
    
            final int cUpdatedRows = ps.executeUpdate();
            if (cUpdatedRows != 1)
            {
                throw new SQLException("CountRequest row could not be inserted");
            }
    
            insertChildCountRecords();
            insertXdem();
        }
        finally
        {
            DatabaseUtil.closeStatementNoThrow(ps);
        }
    }

    /**
     * @throws SQLException
     */
    private void insertChildCountRecords() throws SQLException
    {
        for (final Iterator iCount = iterator(); iCount.hasNext();)
        {
            final EpanCount epanCount = (EpanCount)iCount.next();
            epanCount.insert(this.dbRequest,this.pk);
        }
    }

    private void insertXdem() throws SQLException, LookupException
    {
        if (this.xdemCriteria.isEmpty())
        {
            return;
        }
        this.xdemCriteria.save(this.dbRequest,this.xdemExtSpecID);
    }

    private void tryUpdate() throws JAXBException, ClassCastException, ConcurrentModificationException, SQLException, LookupException
    {
        PreparedStatement ps = null;
        try
        {
            deleteXdem();

            ps = this.dbRequest.prepareStatement(
                "UPDATE CountRequest "+
                "SET "+
                    "topic = ?, "+
                    "clientName = ?, "+
                    "criteriaXML = XMLTYPE(?), "+
                    "xdemExtSpecID = ? "+
                "WHERE "+
                    "countRequestID = ? AND "+
                    "modSerial = ?");
    
            ps.setString(1,this.topic);
            ps.setString(2,this.clientName);
            EpanCountCriteriaAccess.putCriteriaToPreparedStatement(this.criteria,ps,3);
            if (this.xdemCriteria.isEmpty())
            {
                ps.setNull(4,Types.NUMERIC);
            }
            else
            {
                this.xdemExtSpecID = SQLHelpers.getID(this.dbRequest,"ExtSpecID_Seq");
                ps.setInt(4,this.xdemExtSpecID);
            }
            DatalessKeyAccessFactory.getDatalessKeyAccess(this.pk).putKeyToPreparedStatement(this.pk,ps,5);
            ps.setLong(6,this.modSerial.asLong());
    
            /*
             * This is where "optimistic locking" is used.
             * We try to update our row in the database,
             * but only if the row in the database has the
             * same modification-serial-number as it had
             * when we originally read it from the database.
             * We do this by including it in the WHERE clause.
             * Doing the UPDATE will therefore update either
             * zero or one rows; one if the serial number
             * matches, and zero if it doesn't. Therefore,
             * updating zero rows is our indication that
             * a concurrent modification happened to the row
             * in the database.
             */
            final int cUpdatedRows = ps.executeUpdate();
    
            if (cUpdatedRows == 0)
            {
                throw new ConcurrentModificationException();
            }
            else if (cUpdatedRows == 1)
            {
                // OK, the row was saved.
                // We expect this to be the normal case.
            }
            else
            {
                throw new IllegalStateException("More than one row was updated; this shouldn't happen.");
            }
    
            updateChildCountRecords();
            insertXdem();
        }
        finally
        {
            DatabaseUtil.closeStatementNoThrow(ps);
        }
    }

    /**
     * @throws SQLException
     * 
     */
    private void updateChildCountRecords() throws SQLException
    {
        deleteChildCountRecords();
        insertChildCountRecords();
    }

    /**
     * @throws SQLException
     * 
     */
    private void deleteChildCountRecords() throws SQLException
    {
        PreparedStatement ps = null;
        try
        {
            ps = this.dbRequest.prepareStatement(
                "DELETE "+
                "FROM "+
                    "CountQuery "+ 
                "WHERE "+
                    "countRequestID = ?");
    
            DatalessKeyAccessFactory.getDatalessKeyAccess(this.pk).putKeyToPreparedStatement(this.pk,ps,1);
    
            ps.executeUpdate();
        }
        finally
        {
            DatabaseUtil.closeStatementNoThrow(ps);
        }
    }

    /**
     * 
     */
    public void buildEpanCountChildRecords()
    {
        // first remove all existing (old) child records
        this.rEpanCount.clear();

        // use this.criteria to build EpanCount objects and
        // add them to this.rEpanCount
        final BreakOutType breakOut = this.criteria.getBreakOut();

        int indexWithinThis = 0;

        if (breakOut.isGender() && breakOut.isGeography())
        {
            final GeographyType geoType = this.criteria.getGeography();
            if (geoType != null)
            {
                final List rGeo = geoType.getId();
                if (rGeo.isEmpty())
                {
                    // strange case (happens when they try to break out
                    // cont-US or full-US by geography)
                    buildOneEpanCountChildRecord(indexWithinThis++,GenderEnumType.MALE,null);
                    buildOneEpanCountChildRecord(indexWithinThis++,GenderEnumType.FEMALE,null);
                }
                for (final Iterator iGeo = rGeo.iterator(); iGeo.hasNext();)
                {
                    final String geo = (String)iGeo.next();
                    if (geo.length() > 0)
                    {
                        final DatalessKey keyGeo = this.keyAccessGeo.createKeyFromString(geo);
                        buildOneEpanCountChildRecord(indexWithinThis++,GenderEnumType.MALE,keyGeo);
                        buildOneEpanCountChildRecord(indexWithinThis++,GenderEnumType.FEMALE,keyGeo);
                    }
                }
            }
        }
        else if (breakOut.isGender())
        {
            buildOneEpanCountChildRecord(indexWithinThis++,GenderEnumType.MALE,null);
            buildOneEpanCountChildRecord(indexWithinThis++,GenderEnumType.FEMALE,null);
        }
        else if (breakOut.isGeography())
        {
            final GeographyType geoType = this.criteria.getGeography();
            if (geoType != null)
            {
                final List rGeo = geoType.getId();
                if (rGeo.isEmpty())
                {
                    // strange case (happens when they try to break out
                    // cont-US or full-US by geography)
                    buildOneEpanCountChildRecord(indexWithinThis++,null,null);
                }
                for (final Iterator iGeo = rGeo.iterator(); iGeo.hasNext();)
                {
                    final String geo = (String)iGeo.next();
                    if (geo.length() > 0)
                    {
                        final DatalessKey keyGeo = this.keyAccessGeo.createKeyFromString(geo);
                        buildOneEpanCountChildRecord(indexWithinThis++,null,keyGeo);
                    }
                }
            }
        }
        else
        {
            buildOneEpanCountChildRecord(indexWithinThis++,null,null);
        }
    }

    /**
     * @param indexWithinThis
     * @param gender
     * @param keyGeo
     */
    private void buildOneEpanCountChildRecord(final int indexWithinThis, final GenderEnumType gender, final DatalessKey keyGeo)
    {
        final StringBuffer name = new StringBuffer(64);

        this.factoryName.appendDisplayName(gender,keyGeo,name);

        final EpanCount epanCount = new EpanCount(name.toString(),indexWithinThis);

        this.rEpanCount.add(epanCount);
    }

    /**
     * Does each count query and waits for the results.
     * EpanCountDoneListener.done is called when each query completes,
     * and also once (right away) when the request is frozen (with
     * an argument of null).
     * @param listener
     * @throws SQLException
     */
    public void run(final EpanCountDoneListener listener) throws SQLException
    {
        synchronized (this.running)
        {
            if (this.running.isTrue())
            {
                return;
            }
            this.running.set(true);
        }

        // First, update timeRequested column; this will freeze the request
        synchronized (this.dbCount)
        {
            updateTimeSubmitted();
        }

        listener.done(null);

        final BreakOutType breakOut = this.criteria.getBreakOut();

        int indexWithinThis = 0;

        if (breakOut.isGender() && breakOut.isGeography())
        {
            final GeographyType geoType = this.criteria.getGeography();
            if (geoType != null)
            {
                final List rGeo = geoType.getId();
                if (rGeo.isEmpty())
                {
                    // strange case (happens when they try to break out
                    // cont-US or full-US by geography)
                    runOneEpanCount(indexWithinThis++,GenderEnumType.MALE,null,listener);
                    runOneEpanCount(indexWithinThis++,GenderEnumType.FEMALE,null,listener);
                }
                for (final Iterator iGeo = rGeo.iterator(); iGeo.hasNext();)
                {
                    final String geo = (String)iGeo.next();
                    if (geo.length() > 0)
                    {
                        final DatalessKey keyGeo = this.keyAccessGeo.createKeyFromString(geo);
                        runOneEpanCount(indexWithinThis++,GenderEnumType.MALE,keyGeo,listener);
                        runOneEpanCount(indexWithinThis++,GenderEnumType.FEMALE,keyGeo,listener);
                    }
                }
            }
        }
        else if (breakOut.isGender())
        {
            runOneEpanCount(indexWithinThis++,GenderEnumType.MALE,null,listener);
            runOneEpanCount(indexWithinThis++,GenderEnumType.FEMALE,null,listener);
        }
        else if (breakOut.isGeography())
        {
            final GeographyType geoType = this.criteria.getGeography();
            if (geoType != null)
            {
                final List rGeo = geoType.getId();
                if (rGeo.isEmpty())
                {
                    // strange case (happens when they try to break out
                    // cont-US or full-US by geography)
                    runOneEpanCount(indexWithinThis++,null,null,listener);
                }
                for (final Iterator iGeo = rGeo.iterator(); iGeo.hasNext();)
                {
                    final String geo = (String)iGeo.next();
                    if (geo.length() > 0)
                    {
                        final DatalessKey keyGeo = this.keyAccessGeo.createKeyFromString(geo);
                        runOneEpanCount(indexWithinThis++,null,keyGeo,listener);
                    }
                }
            }
        }
        else
        {
            runOneEpanCount(indexWithinThis++,null,null,listener);
        }
        this.running.set(false);
        listener.allDone(this);
    }

    private void updateTimeSubmitted() throws SQLException
    {
        /*
         * Set our internal dateRequested variable
         * to the current time. This will make us
         * frozen. This is kind of kludgy, because
         * the timeRequested column in the database
         * will be set by the database, and so will
         * differ (by a fraction of a millisecond, say)
         * from the value we are setting in memory.
         * This shouldn't cause any problems for
         * our purposes.
         * 
         * Maybe already frozen.
         */
        setFrozen(true);

        PreparedStatement st = null;
        try
        {
            st = this.dbCount.prepareStatement(
                "UPDATE CountRequest "+
                    "SET timeRequested = SYSTIMESTAMP "+
                "WHERE "+
                    "countRequestID = ?");
            DatalessKeyAccessFactory.getDatalessKeyAccess(this.pk).putKeyToPreparedStatement(this.pk,st,1);

            st.executeUpdate();
        }
        finally
        {
            DatabaseUtil.closeStatementNoThrow(st);
            this.dbCount.commit();
        }
    }

    private void runOneEpanCount(final int indexWithinThis, final GenderEnumType gender, final DatalessKey keyGeo, final EpanCountDoneListener listener)
    {
        try
        {
            final StringBuffer sqlCount = new StringBuffer(4*K);
            
            final EpanCountSQLBuilder sqlBuilder = new EpanCountSQLBuilder(this.criteria, this.nContGeoID);
            sqlBuilder.appendQuerySQL(gender,keyGeo,sqlCount);
            appendXdemQuerySQL(sqlCount);

            synchronized (this.dbCount)
            {
                runCountQueryRetry(indexWithinThis,sqlCount);
            }
        }
        catch (final Throwable e)
        {
            synchronized (this.dbCount)
            {
                handleCountError(indexWithinThis,e);
            }
        }

        listener.done(getCount(indexWithinThis));
    }

    private void runCountQueryRetry(final int indexWithinThis, final StringBuffer sqlCount) throws ClassCastException, SQLException, CountAbortedByUser, SummaryTableUpdateInProgress
    {
        try
        {
            runCountQuery(indexWithinThis,sqlCount);
        }
        catch (final SummaryTableUpdateInProgress e)
        {
            e.printStackTrace();
            /*
             * We should be able to handle the case where the
             * PanelistSummary table is currently being updated
             * (by the epanCountUpdate process). We should
             * be able to simply wait a few seconds and try again.
             */
            try
            {
                Thread.sleep(8000);
            }
            catch (final InterruptedException intrpt)
            {
                intrpt.printStackTrace();
                Thread.currentThread().interrupt();
            }

            runCountQuery(indexWithinThis,sqlCount);
            // but if any error happens here, even the same one
            // again, just throw
        }
    }

    private void runCountQuery(final int indexWithinThis, final StringBuffer sqlCount) throws SQLException, CountAbortedByUser, SummaryTableUpdateInProgress
    {
        /*
         * If we are aborting, take a quit exit here;
         * throw exception to indicate abort.
         */
        if (this.abort.isTrue())
        {
            throw new CountAbortedByUser();
        }

        initCountQuery(indexWithinThis,sqlCount);
        final long countResult = runActualCountQuery(sqlCount);
        finishCountQuery(countResult,indexWithinThis);
    }

    private void initCountQuery(final int indexWithinThis, final StringBuffer sqlCount) throws SQLException, CountAbortedByUser, SummaryTableUpdateInProgress
    {
        try
        {
            final StringBuffer sqlRunCount = new StringBuffer(256);
            sqlRunCount.append("UPDATE CountQuery SET ");
            sqlRunCount.append("sql = ?, ");
            sqlRunCount.append("timeStart = SYSTIMESTAMP, ");
            sqlRunCount.append("timeAsOf = (SELECT created FROM PanelistSummaryCreated) ");
            sqlRunCount.append("WHERE ");
            sqlRunCount.append("countRequestID = ? AND indexInRequest = ?");

            synchronized (this.abort)
            {
                this.statementRunning = this.dbCount.prepareStatement(sqlRunCount.toString());
                OracleClobAccess.putClobToPreparedStatement(sqlCount,this.statementRunning,1);
                DatalessKeyAccessFactory.getDatalessKeyAccess(this.pk).putKeyToPreparedStatement(this.pk,this.statementRunning,2);
                this.statementRunning.setInt(3,indexWithinThis);
            }
            this.statementRunning.executeUpdate();
        }
        catch (final SQLException e)
        {
            filterUpdateException(e);
            throw new IllegalStateException();
        }
        finally
        {
            closeStatementAndCommit();
        }
    }

    private long runActualCountQuery(final StringBuffer sqlCount) throws SQLException, CountAbortedByUser, SummaryTableUpdateInProgress
    {
        try
        {
            synchronized (this.abort)
            {
                this.statementRunning = this.dbCount.prepareStatement(sqlCount.toString());
            }
            final ResultSet rs = this.statementRunning.executeQuery();

            long countResult = 0;
            while (rs.next())
            {
                countResult = rs.getLong(1);
            }
            return countResult;
        }
        catch (final SQLException e)
        {
            filterUpdateException(e);
            throw new IllegalStateException();
        }
        finally
        {
            closeStatementAndCommit();
        }
    }

    private void finishCountQuery(final long countResult, final int indexWithinThis) throws SQLException, CountAbortedByUser, SummaryTableUpdateInProgress
    {
        try
        {
            final StringBuffer sqlRunCount = new StringBuffer(256);
            sqlRunCount.append("UPDATE CountQuery SET ");
            sqlRunCount.append("countResult = ? ");
            sqlRunCount.append("WHERE ");
            sqlRunCount.append("countRequestID = ? AND indexInRequest = ?");
    
            synchronized (this.abort)
            {
                this.statementRunning = this.dbCount.prepareStatement(sqlRunCount.toString());
                this.statementRunning.setLong(1,countResult);
                DatalessKeyAccessFactory.getDatalessKeyAccess(this.pk).putKeyToPreparedStatement(this.pk,this.statementRunning,2);
                this.statementRunning.setInt(3,indexWithinThis);
            }
            this.statementRunning.executeUpdate();
        }
        catch (final SQLException e)
        {
            filterUpdateException(e);
            throw new IllegalStateException();
        }
        finally
        {
            closeStatementAndCommit();
        }
    }

    private void closeStatementAndCommit() throws CountAbortedByUser, SummaryTableUpdateInProgress, SQLException
    {
        synchronized (this.abort)
        {
            DatabaseUtil.closeStatementNoThrow(this.statementRunning);
            this.statementRunning = null;
            try
            {
                this.dbCount.commit();
            }
            catch (final SQLException e)
            {
                filterUpdateException(e);
                throw new IllegalStateException();
            }
        }
    }

    private void appendXdemQuerySQL(StringBuffer sqlCount) throws SQLException, LookupException, InvalidXdemCriteria
    {
        if (this.xdemCriteria.isEmpty())
        {
            return;
        }

        sqlCount.append(" AND \npanelistID in \n");
        sqlCount.append("( /* begin XDEM */ \n");
        synchronized (this.dbCount)
        {
            this.xdemCriteria.appendPullSQL(this.dbCount,sqlCount,true,false);
        }
        sqlCount.append("\n/* end XDEM */ )\n");
    }

    private void filterUpdateException(final SQLException e) throws CountAbortedByUser, SummaryTableUpdateInProgress, SQLException
    {
        /*
         * Filter out some error codes that will be useful to us:
         * 1. When the abort method calls this.statementRunning.cancel(),
         * we will get ORA-01013, so translate into our
         * meaningful exception (same exception as throw above),
         * 2. If an update of the PanelistSummary table is in
         * progress (see the epanCountUpdate project) then
         * either ORA-08103 or ORA-00942 will be thrown; we
         * translate these into a meaningful exception.
         */
        switch (e.getErrorCode())
        {
            case OracleError.USER_REQUESTED_CANCEL:
                throw new CountAbortedByUser(e);
            case OracleError.OBJECT_WENT_AWAY:
                throw new SummaryTableUpdateInProgress(e);
            case OracleError.TABLE_NOT_FOUND:
                throw new SummaryTableUpdateInProgress(e);
            default:
                throw e;
        }
    }

    private void handleCountError(final int indexWithinThis, final Throwable error)
    {
        final StringWriter swError = new StringWriter();
        final PrintWriter pwError = new PrintWriter(swError);
        pwError.println(error.getLocalizedMessage());
        error.printStackTrace(pwError);
        pwError.flush();
        final String sErrorMsg = swError.getBuffer().toString();

        System.err.println(sErrorMsg);

        PreparedStatement st = null;
        try
        {
            try
            {
                this.dbCount.commit();
            }
            catch (final Throwable e)
            {
                // we always seem to get an inexplicable case
                // of ORA-01013 for our statement, so prevent
                // that with this "commit, catch, and ignore" block.
                e.printStackTrace();
            }

            final StringBuffer sql = new StringBuffer(K);

            sql.append("UPDATE CountQuery SET ");
            sql.append("timeStart = NVL(timeStart,SYSTIMESTAMP), ");
            sql.append("errorMsg = ? ");
            sql.append("WHERE countRequestID = ? AND indexInRequest = ?");
    
            st = this.dbCount.prepareStatement(sql.toString());

            st.setString(1,sErrorMsg.substring(0,sErrorMsg.length() >= 4000 ? 4000 : sErrorMsg.length()));
            DatalessKeyAccessFactory.getDatalessKeyAccess(this.pk).putKeyToPreparedStatement(this.pk,st,2);
            st.setInt(3,indexWithinThis);

            st.executeUpdate();
        }
        catch (final Throwable errorDuringErrorHandling)
        {
            errorDuringErrorHandling.printStackTrace();
        }
        finally
        {
            DatabaseUtil.closeStatementNoThrow(st);
            try
            {
                this.dbCount.commit();
            }
            catch (final SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Aborts all currently pending or executing queries.
     * Does nothing if no currently pending or executing queries.
     */
    public void abort()
    {
        PreparedStatement st = null;
        synchronized (this.abort)
        {
            if (this.abort.isTrue())
            {
                return;
            }
            this.abort.set(true);
            if (this.statementRunning == null)
            {
                System.err.println("Statement to abort is null.");
                return;
            }
            st = this.statementRunning;
        }
        synchronized (this.dbCount)
        {
            try
            {
                st.cancel();
            }
            catch (final SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Deletes this request from the database. After
     * returning from this call, we assume the caller
     * will not call any more methods on this object.
     * @throws SQLException
     */
    public void delete() throws SQLException
    {
        synchronized (this.dbRequest)
        {
            Savepoint savepoint = null;
            try
            {
                savepoint = this.dbRequest.setSavepoint();
    
                deleteXdem();
                deleteRequest();
    
                savepoint = null;
            }
            finally
            {
                if (savepoint == null)
                {
                    this.dbRequest.commit();
                }
                else
                {
                    try
                    {
                        this.dbRequest.rollback(savepoint);
                    }
                    catch (final Throwable e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void deleteXdem() throws SQLException
    {
        if (this.xdemExtSpecID == 0)
        {
            return;
        }

        PreparedStatement ps = null;
        try
        {
            ps = this.dbRequest.prepareStatement(
                "DELETE FROM XdemCriteria WHERE extSpecID = ?");
            ps.setInt(1,this.xdemExtSpecID);

            ps.executeUpdate();
        }
        finally
        {
            DatabaseUtil.closeStatementNoThrow(ps);
        }
    }

    private void deleteRequest() throws SQLException
    {
        PreparedStatement ps = null;
        try
        {
            ps = this.dbRequest.prepareStatement(
                "DELETE FROM CountRequest WHERE countRequestID = ?");
            DatalessKeyAccessFactory.getDatalessKeyAccess(this.pk).putKeyToPreparedStatement(this.pk,ps,1);

            ps.executeUpdate();
        }
        finally
        {
            DatabaseUtil.closeStatementNoThrow(ps);
        }
    }

	/**
     * Freezes or unfreezes this request.
	 * @param freeze
	 */
	public void setFrozen(final boolean freeze)
	{
		if (freeze)
			this.dateRequested = new Date();
		else
			this.dateRequested = null;
	}
}
