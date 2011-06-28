/*
 * Sets up the new recurring job for epanCountUpdate.
 * The script must be run as DA_Prod (or QC).
 */
DECLARE
    v_recurJobID NUMBER;
    v_recurJobParamValueID NUMBER;
    v_swLevel VARCHAR(7);
    v_schemaCurr VARCHAR2(30);
    v_nameDB VARCHAR2(4000);
    v_osUser VARCHAR2(255);
BEGIN



    /*
     * Job Type
     */

    v_schemaCurr := Sys_Context('userEnv','current_Schema');
    IF (v_schemaCurr = 'QC') THEN
        v_swLevel := 'Beta';
        v_osUser := 'nsuser';
    ELSE
        SELECT property_Value INTO v_nameDB
        FROM DataBase_Properties
        WHERE property_Name = 'GLOBAL_DB_NAME';

        IF (v_nameDB LIKE 'DEV%') THEN
            v_swLevel := 'Alpha';
        ELSE
            v_swLevel := 'Release';
        END IF;
        v_osUser := 'webuser';
    END IF;



    -- add a JobType for epanCountUpdate (recurring job)
	INSERT INTO JobType
	(
	    jobTypeID,
	    name,
	    defaultOSUser,
	    jvmFlags,
        logFileCreationSpec
	)
	VALUES
	(
	    15,
	    'epanCountUpdate',
	    v_osUser,
	    '-Dcom.surveysampling.jdbc.config.bundle=com.surveysampling.emailpanel.counts.update.scheduling.JDBCConfig'||v_swLevel,
	    '${UserHome}/logs/epanCountUpdate/${JobID}.log'
	);



    /*
     * Recurring Job
     */



    -- get next recurJobID
    SELECT NVL(MAX(recurJobID),0)+1 INTO v_recurJobID FROM RecurJob;

    -- add a RecurJob for epanCountUpdate
    INSERT INTO RecurJob
    (
        recurJobID,
        name,
        recurJobStrategyID,
	    jobTypeID,
	    execContextID,
        ssiEmployeeID,
        priority,
        bNotify,
        bActive,
        lastJobID
    )
    VALUES
    (
        v_recurJobID,
	    'epanCountUpdate',
	    2, -- DateSubmissionStrategy
	    15, -- new epanCountUpdate jobType (from above)
	    2, -- local Unix execution context
	    89, -- Chris Mosher
	    1, -- lowest priority
	    'T', -- notify
	    'T', -- active
	    (SELECT MIN(jobID) FROM Job) -- bogus last job
    );


    -- get next recurJobParamValueID
    SELECT NVL(MAX(recurJobParamValueID),0)+1 INTO v_recurJobParamValueID FROM RecurJobParamValue;

    -- insert time to run at, each day
    INSERT INTO RecurJobParamValue
    (
        recurJobParamValueID,
        recurJobParamTypeID,
        recurJobID,
        value
    )
    VALUES
    (
        v_recurJobParamValueID,
        1, -- TimeOfDay
        v_recurJobID,
        '03:18'
    );

    -- get next recurJobParamValueID
    SELECT NVL(MAX(recurJobParamValueID),0)+1 INTO v_recurJobParamValueID FROM RecurJobParamValue;

    -- insert day of week to run on
    INSERT INTO RecurJobParamValue
    (
        recurJobParamValueID,
        recurJobParamTypeID,
        recurJobID,
        value
    )
    VALUES
    (
        v_recurJobParamValueID,
        2, -- DayOfWeek
        v_recurJobID,
        '2' -- 1=Sunday...7=Saturday
    );



    /*
     * Lock Types
     */
    INSERT INTO ResourceType
    (
        resourceTypeID,
        name,
        numberingRule
    )
    VALUES
    (
        12, -- com.surveysampling.emailpanel.lock.LockConstants.RESOURCE_EPAN_COUNT_UPDATE_JOB
        'epanCountUpdate job',
        'N/A'
    );

    INSERT INTO LockOpType
    (
        lockOpTypeID,
        name,
        numberingRule
    )
    VALUES
    (
        14, -- com.surveysampling.emailpanel.lock.LockConstants.LOCK_OP_EPAN_COUNT_UPDATE
        'epanCountUpdate',
        'jobID'
    );



END;

/
