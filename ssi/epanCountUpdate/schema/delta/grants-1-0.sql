/*
 * This script is not run directly at deploy time.
 * Rather, the contents of this script will be copied
 * into the standard Epanel Grant script, and that script
 * run at deploy time. It is here really just for
 * documentation purposes.
 *
 * Note that the grant script is designed to be run under
 * the user that owns the Epanel schema (DA_PROD or QC).
 */

/*
 * The epanCountUpdate process needs to select from our
 * PanelistSummaryCreation view to create its PanelistSummary table.
 */
GRANT SELECT ON PanelistSummaryCreation TO SSI_Email_Count_Update;
