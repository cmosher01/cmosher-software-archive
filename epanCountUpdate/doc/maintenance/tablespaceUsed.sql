/*
 * Determines amount of tablespace used for
 * EpanCount's default tablespace.
 */
SELECT
    tablespaceName,
    totalFreeBlocks,
    largestFreeBlocks,
    totalBlocks,
    ROUND((totalBlocks-totalFreeBlocks)*100/totalBlocks,2) percentUsed
FROM
(
    SELECT
	    MAX(tablespace_Name) tablespaceName,
        SUM(blocks) totalFreeBlocks,
        MAX(blocks) largestFreeBlocks
    FROM
        DBA_Free_Space
    WHERE
        tablespace_Name = (SELECT default_Tablespace FROM DBA_Users WHERE userName='EPANCOUNT')
),
(
    SELECT
        SUM(blocks) totalBlocks 
    FROM
        DBA_Data_Files
    WHERE
        tablespace_Name = (SELECT default_Tablespace FROM DBA_Users WHERE userName='EPANCOUNT')
)
