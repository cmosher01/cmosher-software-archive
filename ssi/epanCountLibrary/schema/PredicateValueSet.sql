CREATE TABLE PredicateValueSet
(
    xdemPredicateID NUMBER(10) NOT NULL, 
    valueID NUMBER(10) NOT NULL, 
    CONSTRAINT AXPKPredicateValueSet PRIMARY KEY (xdemPredicateID,valueID),
    CONSTRAINT PredicateValueSet_predID FOREIGN KEY (xdemPredicateID) REFERENCES XdemPredicate(xdemPredicateID) ON DELETE CASCADE
)
ORGANIZATION INDEX COMPRESS 1;
