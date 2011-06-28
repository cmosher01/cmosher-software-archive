CREATE OR REPLACE TRIGGER CountQuery_UpdateCount
    BEFORE UPDATE
    ON CountQuery
    FOR EACH ROW
DECLARE
BEGIN
    /*
     * if the count or errormsg column is being added, then
     * also set the timeEnd column (unless we already have timeEnd)
     */
    IF (((:OLD.countResult IS NULL AND :NEW.countResult IS NOT NULL) OR
            (:OLD.errorMsg IS NULL AND :NEW.errorMsg IS NOT NULL)) AND
        :OLD.timeEnd IS NULL) THEN
        :NEW.timeEnd := SYSTIMESTAMP;
    END IF;
END;
