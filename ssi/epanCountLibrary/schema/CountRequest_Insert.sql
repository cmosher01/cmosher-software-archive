CREATE OR REPLACE TRIGGER CountRequest_Insert
    BEFORE INSERT
    ON CountRequest
    FOR EACH ROW
DECLARE
BEGIN
    :NEW.timeCreated := SYSTIMESTAMP;
    SELECT userID INTO :NEW.CreatedBy FROM CurrentUserID;
END;
