CREATE UNIQUE INDEX XIE1CountRequest ON CountRequest(modSerial);

CREATE INDEX XIE2CountRequest ON CountRequest(timeCreated);

CREATE UNIQUE INDEX XIE1CountRequestDeleted ON CountRequestDeleted(modSerial);
