CREATE TABLE PredicateVariableSet
(
    xdemPredicateID NUMBER(10) NOT NULL, 
    variableID NUMBER(10) NOT NULL, 
    CONSTRAINT AXPKPredicateVariableSet PRIMARY KEY (xdemPredicateID,variableID),
    CONSTRAINT PredicateVariableSet_predID FOREIGN KEY (xdemPredicateID) REFERENCES XdemPredicate(xdemPredicateID) ON DELETE CASCADE
)
ORGANIZATION INDEX COMPRESS 1;
