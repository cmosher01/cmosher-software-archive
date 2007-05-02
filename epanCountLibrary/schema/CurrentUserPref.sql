CREATE OR REPLACE VIEW CurrentUserPref
AS
SELECT *
FROM UserPref
WHERE userID = (SELECT userID FROM CurrentUserID);
