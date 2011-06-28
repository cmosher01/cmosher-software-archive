CREATE OR REPLACE VIEW CurrentUserID
AS
SELECT
    ssiEmployeeID userID
FROM
    SSIEmployee
WHERE
    userName = UPPER(USER);
