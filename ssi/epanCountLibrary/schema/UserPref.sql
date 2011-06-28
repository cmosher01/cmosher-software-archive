CREATE TABLE UserPref
(
    /*
     * SSIEmployeeID of the user
     */
    userID NUMBER NOT NULL,
    prefName VARCHAR(32) NOT NULL,
    prefValue VARCHAR(256)
);
