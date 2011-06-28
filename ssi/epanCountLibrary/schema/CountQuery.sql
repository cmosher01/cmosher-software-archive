CREATE TABLE CountQuery
(
    /*
     * foreign key to parent CountRequest row
     */
    countRequestID RAW(16) NOT NULL,
    /*
     * index number (1-origin) within CountRequest
     */
    indexInRequest NUMBER(9) NOT NULL,
    /*
     * user-readable identifying name
     * (for example, geographic area and/or gender)
     */
    name VARCHAR2(256),
    /*
     *
     *
     *
     *
     *
     * the resulting count of this count query
     * (if it completed without error)
     * (if both countResult and errorMsg are null,
     * then the query hasn't finished running yet)
     */
    countResult NUMBER(19),
    /*
     * error message if an error occurred
     * when trying to run this count query
     */
    errorMsg VARCHAR2(4000),
    /*
     *
     *
     *
     *
     * timestamp when this query started
     */
    timeStart TIMESTAMP,
    /*
     * timestamp when this query finished
     */
    timeEnd TIMESTAMP,
    /*
     * timestamp of PanelistSummary table
     * creation (i.e., "as of" date) that
     * this count was run against
     */
    timeAsOf TIMESTAMP,
    /*
     * SQL query used to get this count
     */
    sql CLOB,
    /*
     *
     *
     *
     *
     * primary key constraint
     */
    CONSTRAINT AXPKCountQuery PRIMARY KEY (countRequestID,indexInRequest),
    /*
     * foreign key constraint for CountRequest
     */
    CONSTRAINT XCountQuery_countRequest
        FOREIGN KEY (countRequestID)
        REFERENCES CountRequest(countRequestID) 
        ON DELETE CASCADE
);
