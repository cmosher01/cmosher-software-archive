CREATE OR REPLACE VIEW PanKids2Plus AS
SELECT
/*
 * Get HHMember ages, by whole year and gender, for children
 * 2-17 years old.
 */
    panelistID,
    MAX(hasMemberM02) hasMemberM02,
    MAX(hasMemberM03) hasMemberM03,
    MAX(hasMemberM04) hasMemberM04,
    MAX(hasMemberM05) hasMemberM05,
    MAX(hasMemberM06) hasMemberM06,
    MAX(hasMemberM07) hasMemberM07,
    MAX(hasMemberM08) hasMemberM08,
    MAX(hasMemberM09) hasMemberM09,
    MAX(hasMemberM10) hasMemberM10,
    MAX(hasMemberM11) hasMemberM11,
    MAX(hasMemberM12) hasMemberM12,
    MAX(hasMemberM13) hasMemberM13,
    MAX(hasMemberM14) hasMemberM14,
    MAX(hasMemberM15) hasMemberM15,
    MAX(hasMemberM16) hasMemberM16,
    MAX(hasMemberM17) hasMemberM17,
    MAX(hasMemberF02) hasMemberF02,
    MAX(hasMemberF03) hasMemberF03,
    MAX(hasMemberF04) hasMemberF04,
    MAX(hasMemberF05) hasMemberF05,
    MAX(hasMemberF06) hasMemberF06,
    MAX(hasMemberF07) hasMemberF07,
    MAX(hasMemberF08) hasMemberF08,
    MAX(hasMemberF09) hasMemberF09,
    MAX(hasMemberF10) hasMemberF10,
    MAX(hasMemberF11) hasMemberF11,
    MAX(hasMemberF12) hasMemberF12,
    MAX(hasMemberF13) hasMemberF13,
    MAX(hasMemberF14) hasMemberF14,
    MAX(hasMemberF15) hasMemberF15,
    MAX(hasMemberF16) hasMemberF16,
    MAX(hasMemberF17) hasMemberF17
FROM
(
    SELECT
        panelistID,
        DECODE(ageInYears,02,DECODE(genderID,225,'T','F'),'F') hasMemberM02,
        DECODE(ageInYears,03,DECODE(genderID,225,'T','F'),'F') hasMemberM03,
        DECODE(ageInYears,04,DECODE(genderID,225,'T','F'),'F') hasMemberM04,
        DECODE(ageInYears,05,DECODE(genderID,225,'T','F'),'F') hasMemberM05,
        DECODE(ageInYears,06,DECODE(genderID,225,'T','F'),'F') hasMemberM06,
        DECODE(ageInYears,07,DECODE(genderID,225,'T','F'),'F') hasMemberM07,
        DECODE(ageInYears,08,DECODE(genderID,225,'T','F'),'F') hasMemberM08,
        DECODE(ageInYears,09,DECODE(genderID,225,'T','F'),'F') hasMemberM09,
        DECODE(ageInYears,10,DECODE(genderID,225,'T','F'),'F') hasMemberM10,
        DECODE(ageInYears,11,DECODE(genderID,225,'T','F'),'F') hasMemberM11,
        DECODE(ageInYears,12,DECODE(genderID,225,'T','F'),'F') hasMemberM12,
        DECODE(ageInYears,13,DECODE(genderID,225,'T','F'),'F') hasMemberM13,
        DECODE(ageInYears,14,DECODE(genderID,225,'T','F'),'F') hasMemberM14,
        DECODE(ageInYears,15,DECODE(genderID,225,'T','F'),'F') hasMemberM15,
        DECODE(ageInYears,16,DECODE(genderID,225,'T','F'),'F') hasMemberM16,
        DECODE(ageInYears,17,DECODE(genderID,225,'T','F'),'F') hasMemberM17,
        DECODE(ageInYears,02,DECODE(genderID,226,'T','F'),'F') hasMemberF02,
        DECODE(ageInYears,03,DECODE(genderID,226,'T','F'),'F') hasMemberF03,
        DECODE(ageInYears,04,DECODE(genderID,226,'T','F'),'F') hasMemberF04,
        DECODE(ageInYears,05,DECODE(genderID,226,'T','F'),'F') hasMemberF05,
        DECODE(ageInYears,06,DECODE(genderID,226,'T','F'),'F') hasMemberF06,
        DECODE(ageInYears,07,DECODE(genderID,226,'T','F'),'F') hasMemberF07,
        DECODE(ageInYears,08,DECODE(genderID,226,'T','F'),'F') hasMemberF08,
        DECODE(ageInYears,09,DECODE(genderID,226,'T','F'),'F') hasMemberF09,
        DECODE(ageInYears,10,DECODE(genderID,226,'T','F'),'F') hasMemberF10,
        DECODE(ageInYears,11,DECODE(genderID,226,'T','F'),'F') hasMemberF11,
        DECODE(ageInYears,12,DECODE(genderID,226,'T','F'),'F') hasMemberF12,
        DECODE(ageInYears,13,DECODE(genderID,226,'T','F'),'F') hasMemberF13,
        DECODE(ageInYears,14,DECODE(genderID,226,'T','F'),'F') hasMemberF14,
        DECODE(ageInYears,15,DECODE(genderID,226,'T','F'),'F') hasMemberF15,
        DECODE(ageInYears,16,DECODE(genderID,226,'T','F'),'F') hasMemberF16,
        DECODE(ageInYears,17,DECODE(genderID,226,'T','F'),'F') hasMemberF17
    FROM
    (
        SELECT /*+ FULL(P) PARALLEL(P,2,3) FULL(M) PARALLEL(M,2,3) */
            P.panelistID,
            TRUNC(MONTHS_BETWEEN(TRUNC(SYSDATE),M.dateBorn)/12) ageInYears,
            M.genderID
        FROM
            Panelist P
            INNER JOIN PanelistHHMember M ON M.panelistID = P.panelistID
        WHERE
            P.bActive = 'T' AND
            P.countryID = 245 AND
            M.bPanelist = 'F' AND
            M.dateBorn BETWEEN ADD_MONTHS(TRUNC(SYSDATE)+1,-18*12) AND ADD_MONTHS(TRUNC(SYSDATE),-2*12)
    )
)
GROUP BY
    panelistID
WITH READ ONLY;
