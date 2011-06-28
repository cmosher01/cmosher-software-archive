CREATE OR REPLACE TRIGGER CountRequestDeleted_InsUpd
    BEFORE INSERT OR UPDATE
    ON CountRequestDeleted
    FOR EACH ROW
DECLARE
BEGIN
    SELECT ModSerial_Seq.NEXTVAL INTO :NEW.modSerial FROM DUAL;
    :NEW.timeLastMod := SYSTIMESTAMP;
END;
