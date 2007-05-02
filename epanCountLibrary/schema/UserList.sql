CREATE OR REPLACE VIEW UserList
AS
SELECT
    keyUserID,
    nameLast,
    nameFirst,
    dept,
    DECODE(seenUserID,NULL,0,1) seen
FROM
(	
    SELECT
        SSIEmployee.ssiEmployeeID keyUserID,
        SSIEmployee.lastName nameLast,
        SSIEmployee.firstName nameFirst,
        SSIDepartment.name dept
    FROM
        SSIEmployee
        INNER JOIN SSIDepartment ON SSIDepartment.ssiDepartmentID = SSIEmployee.ssiDepartmentID
    WHERE
        bResigned = 'F' AND
    	userName IS NOT NULL AND
        ssiEmployeeID != (SELECT userID FROM CurrentUserID)
) P
LEFT OUTER JOIN
(
    SELECT
        seenUserID
    FROM
        UserSees
    WHERE
        userID = (SELECT userID FROM CurrentUserID)
) S
ON
    P.keyUserID = S.seenUserID
WITH READ ONLY;
