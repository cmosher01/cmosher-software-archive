CREATE OR REPLACE VIEW SeenUser
AS
    SELECT seenUserID
    FROM UserSees
    WHERE userID = (SELECT userID FROM CurrentUserID)
UNION
    SELECT userID FROM CurrentUserID;
