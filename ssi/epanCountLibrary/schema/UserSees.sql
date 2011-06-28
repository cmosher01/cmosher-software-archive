CREATE TABLE UserSees
(
    userID NUMBER NOT NULL,
    seenUserID NUMBER NOT NULL --,
    --CONSTRAINT XUserSees_user FOREIGN KEY (userID) REFERENCES SSIEmployee(ssiEmployeeID),
    --CONSTRAINT XUserSees_seen FOREIGN KEY (seenUserID) REFERENCES SSIEmployee(ssiEmployeeID)
);
