CREATE OR REPLACE TRIGGER CountRequest_InsertUpdate
    BEFORE INSERT OR UPDATE
    ON CountRequest
    FOR EACH ROW
DECLARE
BEGIN
    SELECT ModSerial_Seq.NEXTVAL INTO :NEW.modSerial FROM DUAL;
    :NEW.timeLastMod := SYSTIMESTAMP;
    SELECT userID INTO :NEW.lastModBy FROM CurrentUserID;
END;
