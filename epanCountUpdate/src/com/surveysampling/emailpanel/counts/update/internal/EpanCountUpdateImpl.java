/*
 * Created on March 21, 2005
 */
package com.surveysampling.emailpanel.counts.update.internal;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import com.surveysampling.emailpanel.counts.update.EpanCountUpdateException;

/**
 * Updates the PanelistSummary table.
 * 
 * @author Chris Mosher
 */
public class EpanCountUpdateImpl
{
    /**
     * The name of the PanelistSummary table.
     */
    private static final String tablePanelistSummary = "PanelistSummary";

    /**
     * The (staging) name of the PanelistSummary staging table.
     */
    private static final String tablePanelistSummaryStaging = tablePanelistSummary+"New";

    /**
     * The name of the view from which the PanelistSummary
     * table will be created.
     */
    private static final String viewPanelistSummaryCreation = tablePanelistSummary+"Creation";

    /**
     * The "base" part of the prefix prepended to each column
     * name to make the corresponding index name. For example,
     * for the column <code>DMA</code>, we create the name
     * <code>XBIPANSUMDMA</code> for the corresponding index,
     * and the (staging) name <code>TBIPANSUMDMA</code> for
     * the staging index (subsequently renamed to the real name).
     */
    private static final String indexPrefixBase = "BIPanSum";

    /**
     * The prefix for the name of the indexes.
     */
    private static final String indexPrefix = "X"+indexPrefixBase;

    /**
     * The prefix for the staging name of the indexes.
     */
    private static final String indexPrefixStaging = "T"+indexPrefixBase;

    /**
     * The overhead on top of the column name length that we will
     * be adding in order to generate the index names. It will be
     * the maximum length of <code>indexPrefix</code> or <code>indexPrefixStaging</code>.
     */
    private static final int indexNameOverhead = Math.max(indexPrefixStaging.length(),indexPrefix.length());



    /**
     * The database <code>Connection</code> to use for all
     * database activity.
     */
    private final Connection db;

    /**
     * The <code>Logger</code> to write progress messages to.
     */
    private final Logger log;



    /**
     * Initializes an instance of this class. This object assumes
     * that the database <code>Connection</code> has been properly
     * set up. This means that it should be connected to the
     * correct database, as the correct user-name, with current schema
     * set correctly (that is, left at the default), and with the
     * proper role enabled (SSI_Email_Count_Update, in order to select
     * from PanelistSummaryCreation view).
     * @param db database <code>Connection</code> to use
     * @param log <code>Logger</code> to log progress messages to
     */
    public EpanCountUpdateImpl(final Connection db, final Logger log)
    {
        this.db = db;
        this.log = log;
    }

    /**
     * Updates the PanelistSummary table.
     * @throws SQLException
     * @throws IOException
     * @throws EpanCountUpdateException
     */
    public void update() throws SQLException, IOException, EpanCountUpdateException
    {
        log.info("Beginning the PanelistSummary update process...");

        final Set setColumns = getColumnNames();

        createStagingTable(setColumns);

        makeStagingTableLive(setColumns);

        log.info("Finished the PanelistSummary update process.");
    }



    /**
     * Gets the names of all columns in the PanelistSummary
     * table that we will be creating, except for the primary
     * key column panelistID.
     * @return <code>Set</code> of column names (<code>String</code>s).
     * @throws SQLException
     * @throws EpanCountUpdateException
     */
    private Set getColumnNames() throws SQLException, EpanCountUpdateException
    {
        /*
         * Get all column names from the PanelistSummaryNew table, and
         * remove the primary key (panelistID) column.
         */
        final Set setColumns = OracleSQLUtil.getColumnNames(viewPanelistSummaryCreation,db);
        setColumns.remove("PANELISTID");
        if (setColumns.size() == 0)
        {
            throw new EpanCountUpdateException("Cannot find columns for "+viewPanelistSummaryCreation);
        }

        /*
         * Check the length of each column name, to make sure that it
         * is not too long. We will be using the column name appended
         * to some prefix to build the name of the corresponding index.
         */
        for (final Iterator i = setColumns.iterator(); i.hasNext();)
        {
            final String columnName = (String)i.next();
            if (columnName.length() > OracleSQLUtil.MAX_INDEX_NAME_LENGTH-indexNameOverhead)
            {
                throw new IllegalStateException("Column name "+columnName+" is too long; can be "+
                    (OracleSQLUtil.MAX_INDEX_NAME_LENGTH-indexNameOverhead)+" characters maximum.");
            }
        }

        return setColumns;
    }

    /**
     * Performs the steps necessary to create the new staging table,
     * indexes, and histograms.
     * @param setColumns
     * @throws SQLException
     * @throws IOException
     */
    private void createStagingTable(final Set setColumns) throws SQLException, IOException
    {
        //drop any existing PanelistSummaryNew table
        OracleSQLUtil.dropTable(tablePanelistSummaryStaging,db);

        //create PanelistSummaryNew table, from PanelistSummaryCreation view
        createStagingPanelistSummaryTable();

        //create new bitmap index on each column
        for (final Iterator i = setColumns.iterator(); i.hasNext();)
        {
            final String columnName = (String)i.next();
            createStagingIndexOnColumn(columnName);
        }

        //update Oracle statistics (histograms) on PanelistSummaryNew table (and indexes)
        log.info("Gathering statistics for table and indexes.");
        OracleSQLUtil.createHistogramsStatsCascade(tablePanelistSummaryStaging,db);
        log.info("Done gathering statistics for table and indexes.");
    }

    /**
     * Performs the steps necessary to remove the existing live
     * table, and swap in the new one (from the staging name).
     * @param setColumns
     * @throws SQLException
     */
    private void makeStagingTableLive(final Set setColumns) throws SQLException
    {
        /*
         * Up until this point, queries were unaffected.
         * The creation of the new table, indexes, and
         * statistics above was inconsequential.
         */

        //drop any existing PanelistSummary table (and indexes and statistics)
        log.info("Dropping table "+tablePanelistSummary);
        OracleSQLUtil.dropTable(tablePanelistSummary,db);
        /*
         * BEGIN queries fail
         *     Now that the live table has been dropped,
         *     any pre-existing queries fail with: ORA-08103: object no longer exists, and
         *     any new queries fail with: ORA-00942: table or view does not exist.
         */

        //rename PanelistSummaryNew to PanelistSummary
        renameStagingTable();

        //grant read access on PanelistSummary to SSI_Email_Count role
        grantReadOnlyAccessToSummaryTable();
        /*
         * END queries fail
         * Now all new queries should work fine.
         * The following index renames are inconsequential.
         */

        //rename bitmap index TBIPanSumCOLUMN to XBIPanSumCOLUMN for each column
        for (final Iterator i = setColumns.iterator(); i.hasNext();)
        {
            final String columnName = (String)i.next();
            renameIndex(columnName);
        }
    }



    /**
     * Creates the PanelistSummary table, but under a staging name.
     * @throws SQLException
     */
    private void createStagingPanelistSummaryTable() throws SQLException
    {
        final String createTable =
            "CREATE TABLE "+tablePanelistSummaryStaging+" "+
            "PCTFREE 0 PCTUSED 99 MAXTRANS 2 NOLOGGING "+
            "AS SELECT * FROM "+viewPanelistSummaryCreation;

        log.info("Creating new summary table: "+tablePanelistSummaryStaging);

        executeDDL(createTable);

        log.info("Done creating new summary table: "+tablePanelistSummaryStaging);
    }

    /**
     * Creates an index on the given column, with a staging name.
     * @param columnName
     * @throws SQLException
     */
    private void createStagingIndexOnColumn(final String columnName) throws SQLException
    {
        final String indexName = indexPrefixStaging+columnName;

        final String createIndex =
            "CREATE BITMAP INDEX "+indexName+" "+
            "ON "+tablePanelistSummaryStaging+"("+columnName+") "+
            "PARALLEL 9 "+
            "PCTFREE 0 INITRANS 2 MAXTRANS 2 NOLOGGING";

        log.info("Creating index "+indexName+" on column "+tablePanelistSummaryStaging+"."+columnName);

        executeDDL(createIndex);

        log.info("Done creating index "+indexName+" on column "+tablePanelistSummaryStaging+"."+columnName);
    }

    /**
     * Renames the staging table to the live table.
     * @throws SQLException
     */
    private void renameStagingTable() throws SQLException
    {
        final String renameTable =
            "RENAME "+tablePanelistSummaryStaging+" TO "+tablePanelistSummary;

        log.info("Renaming table "+tablePanelistSummaryStaging+" to "+tablePanelistSummary);

        executeDDL(renameTable);
    }

    /**
     * Grants the necessary privileges on the live table to
     * the SSI_Email_Count role.
     * @throws SQLException
     */
    private void grantReadOnlyAccessToSummaryTable() throws SQLException
    {
        final String renameTable =
            "GRANT SELECT ON "+tablePanelistSummary+" TO SSI_Email_Count";

        log.info("Granting read-only access to table "+tablePanelistSummary);

        executeDDL(renameTable);
    }

    /**
     * Renames the index on the given column from the
     * staging name to the live name.
     * @param columnName
     * @throws SQLException
     */
    private void renameIndex(final String columnName) throws SQLException
    {
        final String indexNameStaging = indexPrefixStaging+columnName;
        final String indexNameLive = indexPrefix+columnName;

        final String renameIndex =
            "ALTER INDEX "+indexNameStaging+" RENAME TO "+indexNameLive;

        log.info("Renaming index "+indexNameStaging+" to "+indexNameLive);

        executeDDL(renameIndex);
}

    /**
     * Executes the given DDL statement.
     * @param DDL the DDL statement to execute
     * @throws SQLException
     */
    private void executeDDL(final String DDL) throws SQLException
    {
        OracleSQLUtil.executeDDL(DDL, db);
    }
}
