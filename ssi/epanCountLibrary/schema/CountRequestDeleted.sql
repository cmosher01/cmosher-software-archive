CREATE TABLE CountRequestDeleted
(
    countRequestID RAW(16) NOT NULL,
    timeLastMod TIMESTAMP NOT NULL,
    modSerial NUMBER NOT NULL,
    createdBy NUMBER NOT NULL
);
