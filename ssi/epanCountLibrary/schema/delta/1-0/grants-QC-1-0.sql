/*
 * This is incorporated into the normal Epanel Grant
 * script for this release. That script is run as
 * user DA_Prod or QC.
 */
GRANT SELECT ON SSIEmployee TO SSI_Email_Count;

GRANT SELECT ON SSIEmployee TO EpanCountQC WITH GRANT OPTION;

GRANT SELECT ON SSIDepartment TO EpanCountQC WITH GRANT OPTION;

GRANT SELECT ON XdemPullable TO SSI_Email_Count;

GRANT SELECT ON Referent TO SSI_Email_Count;

GRANT SELECT ON ReferentArc TO SSI_Email_Count;

GRANT SELECT ON ReferentLevel TO SSI_Email_Count;

GRANT SELECT ON ReferentSpan TO SSI_Email_Count;

GRANT SELECT ON Value TO SSI_Email_Count;

GRANT SELECT ON ValueArc TO SSI_Email_Count;

GRANT SELECT ON ValueLevel TO SSI_Email_Count;

GRANT SELECT ON ValueSpan TO SSI_Email_Count;

GRANT SELECT ON Variable TO SSI_Email_Count;

GRANT SELECT ON VariableArc TO SSI_Email_Count;

GRANT SELECT ON VariableLevel TO SSI_Email_Count;

GRANT SELECT ON VariableSpan TO SSI_Email_Count;

GRANT SELECT ON VariableTopic TO SSI_Email_Count;
