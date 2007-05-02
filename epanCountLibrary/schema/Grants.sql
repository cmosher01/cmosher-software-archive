/*
 * Stored procedures
 */
GRANT EXECUTE ON KeyValueUUID TO SSI_Email_Count;

GRANT EXECUTE ON KeyValueLong TO SSI_Email_Count;



/*
 * Sequences
 */
GRANT SELECT ON ModSerial_Seq TO SSI_Email_Count;

GRANT SELECT ON ExtSpecID_Seq TO SSI_Email_Count;

GRANT SELECT ON XdemCriteriaID_Seq TO SSI_Email_Count;

GRANT SELECT ON XdemPredicateID_Seq TO SSI_Email_Count;



/*
 * Tables
 */
GRANT SELECT,INSERT,UPDATE,DELETE ON CountRequest TO SSI_Email_Count;

GRANT SELECT,INSERT,UPDATE ON CountRequestDeleted TO SSI_Email_Count;

GRANT SELECT,INSERT,UPDATE,DELETE ON CountQuery TO SSI_Email_Count;

GRANT SELECT,INSERT,UPDATE,DELETE ON UserSees TO SSI_Email_Count;

GRANT SELECT,INSERT,UPDATE,DELETE ON UserPref TO SSI_Email_Count;

GRANT SELECT,INSERT,UPDATE,DELETE ON XdemCriteria TO SSI_Email_Count;

GRANT SELECT,INSERT,UPDATE,DELETE ON XdemPredicate TO SSI_Email_Count;

GRANT SELECT,INSERT,UPDATE,DELETE ON CriteriaReferentSet TO SSI_Email_Count;

GRANT SELECT,INSERT,UPDATE,DELETE ON PredicateValueSet TO SSI_Email_Count;

GRANT SELECT,INSERT,UPDATE,DELETE ON PredicateVariableSet TO SSI_Email_Count;



/*
 * Views
 */
GRANT SELECT ON CurrentUserID TO SSI_Email_Count;

GRANT SELECT ON CurrentUserPref TO SSI_Email_Count;

GRANT SELECT ON OldestRequestTime TO SSI_Email_Count;

GRANT SELECT ON SeenUser TO SSI_Email_Count;

GRANT SELECT ON CountRequestQueryStatus TO SSI_Email_Count;

GRANT SELECT ON EpanCountList TO SSI_Email_Count;

GRANT SELECT ON PanelistSummaryCreated TO SSI_Email_Count;

GRANT SELECT ON UserList TO SSI_Email_Count;
