SET ECHO ON;

WHENEVER SQLERROR EXIT SQL.SQLCODE;


/*
 * Create synonyms
 */
@..\..\Synonyms.sql
/


/*
 * Create stored procedures
 */
@..\..\KeyValueLong.sql
/
@..\..\KeyValueUUID.sql
/



/*
 * Create sequences
 */
@..\..\ModSerial_Seq.sql

@..\..\XdemCriteriaID_Seq.sql

@..\..\XdemPredicateID_Seq.sql

@..\..\ExtSpecID_Seq.sql



/*
 * Create tables
 */
@..\..\CountRequest.sql

@..\..\CountRequestDeleted.sql

@..\..\CountQuery.sql

@..\..\UserSees.sql

@..\..\UserPref.sql

@..\..\XdemCriteria.sql

@..\..\XdemPredicate.sql

@..\..\CriteriaReferentSet.sql

@..\..\PredicateValueSet.sql

@..\..\PredicateVariableSet.sql



/*
 * Create indexes
 */
@..\..\Indexes.sql



/*
 * Create views
 */
@..\..\CurrentUserID.sql

@..\..\CurrentUserPref.sql

@..\..\OldestRequestTime.sql

@..\..\SeenUser.sql

@..\..\CountRequestQueryStatus.sql

@..\..\EpanCountList.sql

@..\..\PanelistSummaryCreated.sql

@..\..\UserList.sql



/*
 * Create triggers
 */
@..\..\CountRequest_Insert.sql
/
@..\..\CountRequest_InsertUpdate.sql
/
@..\..\CountRequest_Delete.sql
/
@..\..\CountQuery_InsertUpdateDelete.sql
/
@..\..\CountQuery_UpdateCount.sql
/
@..\..\CountRequestDeleted_InsertUpdate.sql
/



/*
 * Grant privileges
 */
@..\..\Grants.sql
/
