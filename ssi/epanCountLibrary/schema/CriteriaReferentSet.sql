CREATE TABLE CriteriaReferentSet
(
    xdemCriteriaID NUMBER(10) NOT NULL,
    referentID NUMBER(10) NOT NULL,
    CONSTRAINT AXPKCriteriaReferentSet PRIMARY KEY (xdemCriteriaID,referentID),
    CONSTRAINT CriteriaReferentSet_criteriaID FOREIGN KEY (xdemCriteriaID) REFERENCES XdemCriteria(xdemCriteriaID) ON DELETE CASCADE
)
ORGANIZATION INDEX COMPRESS 1;
