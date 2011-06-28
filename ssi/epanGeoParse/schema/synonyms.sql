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
        'CREATE SYNONYM Country FOR '||
        v_schemaProd||'.Country';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM GeoType FOR '||
        v_schemaProd||'.GeoType';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM Geo FOR '||
        v_schemaProd||'.Geo';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM GeoAbbreviation FOR '||
        v_schemaProd||'.GeoAbbreviation';
    EXECUTE IMMEDIATE
        'CREATE SYNONYM GeoHierarchy FOR '||
        v_schemaProd||'.GeoHierarchy';
END;
