/*
 * Created on April 22, 2005
 */
package com.surveysampling.emailpanel.counts.api;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.xml.bind.JAXBException;

import com.surveysampling.emailpanel.counts.api.criteria.EpanCountDisplayNameBuilder;
import com.surveysampling.emailpanel.counts.api.criteria.EpanCountReportBuilder;
import com.surveysampling.emailpanel.counts.api.geography.GeographicCodeParser;
import com.surveysampling.emailpanel.counts.api.geography.GeographicNameParser;
import com.surveysampling.emailpanel.counts.api.internal.sql.EpanCountDataSource;
import com.surveysampling.emailpanel.counts.api.list.EpanCountList;
import com.surveysampling.emailpanel.counts.api.list.users.UserAccess;
import com.surveysampling.emailpanel.counts.api.request.EpanCountRequest;
import com.surveysampling.emailpanel.counts.api.request.internal.NonContinentalGeoID;
import com.surveysampling.emailpanel.xdem.metadata.AddFailedException;
import com.surveysampling.emailpanel.xdem.metadata.Metadata;
import com.surveysampling.emailpanel.xdem.metadata.MetadataBuilder;
import com.surveysampling.util.ProgressWatcher;
import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
import com.surveysampling.util.key.DatalessKeyAccessFactory;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;
import com.surveysampling.uuid.UUID;

/**
 * Handles queries for counts of panelists in the Spot database.
 * This class is the main entry point for the API library.
 *
 * @author Chris Mosher
 */
public class EpanCountLibrary
{
    private boolean closed;
    private final Connection db;
    private final DatalessKeyAccess keyAccess;
    private final GeographicNameParser geoNameParser;
    private final GeographicCodeParser geoCodeParser;
    private final EpanCountDisplayNameBuilder builderDisplayName;
    private final EpanCountReportBuilder builderReport;
    private final Metadata xdemMetadata;
    private final UserAccess userAccess;
    private final NonContinentalGeoID nContGeoID;

    /**
     * @param username
     * @param password
     * @throws DatalessKeyAccessCreationException
     * @throws AddFailedException
     * @throws SQLException
     */
    public EpanCountLibrary(final String username, final String password) throws DatalessKeyAccessCreationException, AddFailedException, SQLException
    {
        this(
            username,
            password,
            new ProgressWatcher()
            {
                public void setTotalSteps(int p) {}
                public void setProgress(int p, String s) {}
            });
    }
    /**
     * @param username
     * @param password
     * @param progressWatcher
     * @throws DatalessKeyAccessCreationException
     * @throws SQLException
     * @throws AddFailedException
     */
    public EpanCountLibrary(final String username, final String password, final ProgressWatcher progressWatcher) throws DatalessKeyAccessCreationException, AddFailedException, SQLException
    {
        int progress = 0;
        progressWatcher.setTotalSteps(5);// will call setProgress 5 times

        progressWatcher.setProgress(progress++,"Connecting to database");
        this.db = connectToDatabase(username,password);

        progressWatcher.setProgress(progress++,"Generating random seed");
        initSecureRandom();

        // all (well, most) of the EpanCountLibrary uses UUIDs for primary keys
        this.keyAccess = DatalessKeyAccessFactory.createDatalessKeyAccess("UUID");

        progressWatcher.setProgress(progress++,"Reading geographic names");
        this.geoNameParser = new GeographicNameParser(this.db);
        progressWatcher.setProgress(progress++,"Reading geographic codes");
        this.geoCodeParser = new GeographicCodeParser(this.db);

        progressWatcher.setProgress(progress++,"Reading XDem metadata");
        synchronized (this.db)
        {
            this.xdemMetadata = MetadataBuilder.build(this.db,true);
            this.db.commit();
        }

        this.nContGeoID = new NonContinentalGeoID(this.db, keyAccess);
        
        this.builderDisplayName = new EpanCountDisplayNameBuilder(this.geoNameParser,this.geoCodeParser);

        this.builderReport = new EpanCountReportBuilder(this.geoNameParser,this.geoCodeParser);

        this.userAccess = new UserAccess(this.db);
    }

    private static Connection connectToDatabase(final String username, final String password) throws SQLException
    {
        final DataSource dataSource = EpanCountDataSource.createDataSource();

        return dataSource.getConnection(username,password);
    }

    /**
     * Generates one UUID, which in turn forces the
     * UUID's internal <code>SecureRandom</code> to
     * generated its seed, which is a time-consuming
     * process.
     */
    private static void initSecureRandom()
    {
        new UUID();
    }

    /**
     * @throws SQLException
     * 
     */
    public synchronized void close() throws SQLException
    {
        if (this.closed)
        {
            return;
        }
        this.closed = true;

        synchronized (this.db)
        {
            this.db.close();
        }
    }

    /**
     * @return Returns <code>true</code> if <code>close</code> has been called.
     */
    public synchronized boolean isClosed()
    {
        return this.closed;
    }

    /**
     * @return a new <code>EpanCountList</code>
     */
    public EpanCountList createEpanCountList()
    {
        verifyOpen();
        return new EpanCountList(this.db,this.keyAccess);
    }

    /**
     * @return <code>UserAccess</code> object to manage the set of other
     * users' count that the current user will see
     */
    public UserAccess getUserAccess()
    {
        return this.userAccess;
    }

    /**
     * @param pk
     * @return
     * @throws SQLException
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     */
    public EpanCountRequest readEpanCountRequest(final DatalessKey pk) throws SQLException, JAXBException, DatalessKeyAccessCreationException
    {
        verifyOpen();
        return new EpanCountRequest(pk,this.db,this.db,this.keyAccess,this.builderDisplayName,this.xdemMetadata, this.nContGeoID);
    }

    /**
     * @return
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     */
    public EpanCountRequest createEpanCountRequest() throws JAXBException, DatalessKeyAccessCreationException
    {
        verifyOpen();
        return new EpanCountRequest(this.db,this.db,this.keyAccess,this.builderDisplayName,this.xdemMetadata, this.nContGeoID);
    }

    /**
     * @return the <code>GeographicNameParser</code>
     */
    public GeographicNameParser getGeographicNameParser()
    {
        verifyOpen();
        return this.geoNameParser;
    }

    /**
     * @return the <code>GeographicCodeParser</code>
     */
    public GeographicCodeParser getGeographicCodeParser()
    {
        verifyOpen();
        return this.geoCodeParser;
    }

    /**
     * @return the report builder
     */
    public EpanCountReportBuilder getCountReportBuilder()
    {
        verifyOpen();
        return this.builderReport;
    }

    /**
     * @return the XDem metadata
     */
    public Metadata getXdemMetadata()
    {
        verifyOpen();
        return this.xdemMetadata;
    }

    /**
     * @throws IllegalStateException
     */
    private void verifyOpen() throws IllegalStateException
    {
        if (isClosed())
        {
            throw new IllegalStateException("EpanCountLibrary has already been closed.");
        }
    }
}
