CREATE TABLE XdemPredicate
(
    xdemPredicateID NUMBER(10) NOT NULL,
    xdemCriteriaID NUMBER(10) NOT NULL,
    andOrVariable NUMBER(10) NOT NULL,
    CONSTRAINT AXPKXdemPredicate PRIMARY KEY (xdemPredicateID),
    CONSTRAINT XdemPredicate_criteriaID FOREIGN KEY (xdemCriteriaID) REFERENCES XdemCriteria(xdemCriteriaID) ON DELETE CASCADE,
    CONSTRAINT BETWEEN_0_2132 CHECK (andOrVariable BETWEEN 0 AND 2)
)
ORGANIZATION INDEX;
