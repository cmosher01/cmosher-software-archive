/*
 * Gathers statistics, including histograms, for a
 * table in the current schema.
 *
 * statement parameters:
 * 1. table name
 */
BEGIN
    DBMS_STATS.GATHER_TABLE_STATS
    (
        ownname          => Sys_Context('userEnv','current_Schema'),
        tabname          => ?,
        estimate_percent => 100,
        method_opt       => 'FOR ALL COLUMNS SIZE 254',
        cascade          => TRUE,
        degree           => 9
    );
END;
