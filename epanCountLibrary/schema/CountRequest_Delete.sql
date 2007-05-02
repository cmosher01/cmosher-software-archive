CREATE OR REPLACE TRIGGER CountRequest_Delete
    BEFORE DELETE
    ON CountRequest
    FOR EACH ROW
DECLARE
BEGIN
    INSERT INTO CountRequestDeleted(countRequestID,createdBy)
        VALUES (:OLD.countRequestID,:OLD.createdBy);
END;
