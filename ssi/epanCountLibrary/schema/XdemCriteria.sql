CREATE TABLE XdemCriteria
(
    xdemCriteriaID NUMBER(10) NOT NULL,
    extSpecID NUMBER(10),
    maxMonthsOld NUMBER(10),
    parentCriteriaID NUMBER(10),
    groupType NUMBER(10) NOT NULL,
    andOrReferent NUMBER(10),
    cludeType NUMBER(10),
    CONSTRAINT AXPKXdemCriteria PRIMARY KEY (xdemCriteriaID),
    CONSTRAINT XdemCriteria_parentID FOREIGN KEY (parentCriteriaID) REFERENCES XdemCriteria(xdemCriteriaID) ON DELETE CASCADE,
    CONSTRAINT BETWEEN_0_2129 CHECK (groupType BETWEEN 0 AND 2),
    CONSTRAINT BETWEEN_0_2130 CHECK (andOrReferent BETWEEN 0 AND 2),
    CONSTRAINT BETWEEN_0_2131 CHECK (cludeType BETWEEN 0 AND 2),
    CONSTRAINT XCRIT_LEAFATTRIBS32 CHECK (groupType <> 0 OR (andOrReferent IS NOT NULL AND cludeType IS NOT NULL)),
    CONSTRAINT XCRIT_NONLEAFATTRIBS32 CHECK (groupType = 0 OR (andOrReferent IS NULL AND cludeType IS NULL)),
    CONSTRAINT XCRIT_NONROOTATTRIBS33 CHECK (parentCriteriaID IS NULL OR (extSpecID IS NULL AND maxMonthsOld IS NULL)),
    CONSTRAINT XCRIT_ROOTATTRIBS33 CHECK (parentCriteriaID IS NOT NULL OR (extSpecID IS NOT NULL AND maxMonthsOld IS NOT NULL)),
    CONSTRAINT XAK1XdemCriteria UNIQUE (extSpecID)
);
