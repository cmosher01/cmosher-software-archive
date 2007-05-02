/*
 * This script must be run as the EpanCount[QC] user.
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
        'CREATE SYNONYM XdemPullable FOR '||
        v_schemaProd||'.XdemPullable';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM SSIEmployee FOR '||
        v_schemaProd||'.SSIEmployee';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM SSIDepartment FOR '||
        v_schemaProd||'.SSIDepartment';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM VariableTopic FOR '||
        v_schemaProd||'.VariableTopic';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM VariableSpan FOR '||
        v_schemaProd||'.VariableSpan';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM ValueSpan FOR '||
        v_schemaProd||'.ValueSpan';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM ReferentSpan FOR '||
        v_schemaProd||'.ReferentSpan';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM VariableLevel FOR '||
        v_schemaProd||'.VariableLevel';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM ValueLevel FOR '||
        v_schemaProd||'.ValueLevel';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM ReferentLevel FOR '||
        v_schemaProd||'.ReferentLevel';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM VariableArc FOR '||
        v_schemaProd||'.VariableArc';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM ValueArc FOR '||
        v_schemaProd||'.ValueArc';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM ReferentArc FOR '||
        v_schemaProd||'.ReferentArc';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM Variable FOR '||
        v_schemaProd||'.Variable';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM Value FOR '||
        v_schemaProd||'.Value';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM Referent FOR '||
        v_schemaProd||'.Referent';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM Panelist FOR PanelistSummary';
END;
