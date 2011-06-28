/*
 * This script must be run as the EpanCount
 * user. This synonym allows the epanCountUpdate
 * process to select from the correct
 * PanelistSummaryCreation view.
 */

DECLARE
    v_schemaCurr VARCHAR2(30);
    v_schemaProd VARCHAR2(30);
BEGIN
    v_schemaCurr := Sys_Context('userEnv','current_Schema');
    IF (v_schemaCurr LIKE '%QC') THEN
        v_schemaProd := 'QC';
    ELSE
        v_schemaProd := 'DA_Prod';
    END IF;
    EXECUTE IMMEDIATE
        'CREATE SYNONYM PanelistSummaryCreation FOR '||
        v_schemaProd||'.PanelistSummaryCreation';
END;
