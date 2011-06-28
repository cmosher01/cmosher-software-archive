CREATE OR REPLACE TRIGGER CountQuery_InsertUpdateDelete
    BEFORE INSERT OR UPDATE OR DELETE
    ON CountQuery
    FOR EACH ROW
DECLARE
BEGIN
    /* Just cause parent CountRequest modification trigger to fire: */
    IF (:NEW.countRequestID IS NOT NULL) THEN
        UPDATE CountRequest SET modSerial = 0
            WHERE countRequestID = :NEW.countRequestID;
    END IF;
END;
