CREATE TABLE CountRequest
(
    /*
     * Information about the creation of this record.
     *
     * primary key (UUID)
     */
    countRequestID RAW(16) DEFAULT SYS_GUID() NOT NULL,
    /*
     * Time (set by CountRequest_Insert trigger)
     */
    timeCreated TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    /*
     * SSIEmployeeID (set by CountRequest_Insert trigger)
     */
    createdBy NUMBER NOT NULL,
    /*
     *
     *
     * Information about the latest modification to this record
     * (set by CountRequest_InsertUpdate trigger).
     *
     * Modification serial number
     */
    modSerial NUMBER NOT NULL,
    /*
     * Time
     */
    timeLastMod TIMESTAMP NOT NULL,
    /*
     * SSIEmployeeID
     */
    lastModBy NUMBER NOT NULL,
    /*
     *
     *
     *
     *
     * topic name for this request
     * (used for display purposes only)
     */
    topic VARCHAR2(64) NOT NULL,
    /*
     * client name for this request
     * (used for display purposes only)
     */
    clientName VARCHAR2(64) NOT NULL,
    /*
     * criteria for the counts (geography,
     * demography, etc.)
     */
    criteriaXML XMLTYPE NOT NULL,
    /*
     * Xdem criteria (this is a bogus
     * extSpecID that is used just to join
     * with XdemCriteria.extSpecID)
     */
    xdemExtSpecID NUMBER(10),
    /*
     * timestamp when user requested the count queries
     */
    timeRequested TIMESTAMP,
    /*
     *
     *
     *
     *
     *
     *
     * primary key constraint
     */
    CONSTRAINT AXPKCountRequest PRIMARY KEY (countRequestID)
);
