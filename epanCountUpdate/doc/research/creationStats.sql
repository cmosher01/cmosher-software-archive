-- TABLE



/*
 * Estimates bytes in PanelistSummary table
 * Assume about half the Panelist table is active US.
 * Assume average size of columns in PanelistSummary table is 3 bytes.
 * Note: need to re-evaluate this number.
 */
SELECT ROUND(
(SELECT NUM_ROWS/2 FROM USER_TABLES WHERE TABLE_NAME='PANELIST')
*(SELECT COUNT(*)*3 FROM USER_TAB_COLUMNS WHERE TABLE_NAME='PANELISTSUMMARYCREATION'))
FROM DUAL

/*
 * Gets the number of extents in the PanelistSummaryNew table
 * (after the table has been created). Ideally, we'd like to
 * have the whole table fit into one extent. Use this after table
 * creation to warn the user is there is more than one extent.
 * UPDATE: for now, we ignore all extent stuff and let Oracle
 * handle it by itself.
 */
SELECT COUNT(*) FROM USER_EXTENTS WHERE SEGMENT_NAME='PANELISTSUMMARYNEW'

/*
 * Gets the actual number of bytes used by the PanelistSummaryNew table
 * (after the table has been created). Use this to suggest a value to
 * user for INITIAL_EXTENT for the next run???
 */
SELECT BLOCKS*(SELECT VALUE FROM V$PARAMETER WHERE NAME = 'db_block_size') FROM USER_TABLES WHERE TABLE_NAME='PANELISTSUMMARYNEW'






-- INDEXES



SELECT SEGMENT_NAME,COUNT(*) FROM USER_EXTENTS WHERE SEGMENT_NAME LIKE 'XBIPANSUM%' GROUP BY SEGMENT_NAME
