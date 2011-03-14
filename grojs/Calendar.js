/*
BASED ON:
       JavaScript functions for the Fourmilab Calendar Converter

                  by John Walker  --  September, MIM
              http://www.fourmilab.ch/documents/calendar/

                This program is in the public domain.
*/
/**
 * @fileoverview
 * Defines the {@link Calendar} class.
 */

/**
 * @class Contains static calendar utilities.
 * @requires YMD
 * 
 * @constructor
 * @return never returns
 * @throws
 * @type Calendar
 */
function Calendar() {
	throw new Error("cannot instantiate");
}

/**
 * Converts Gregorian date to JD
 * @param {YMD} ymd
 * @return JD of given Gregorian date
 * @type Number
 */
Calendar.gregorian_to_jd = function(ymd) {
	return gregorian_to_jd(ymd.getYear(),ymd.getMonth(),ymd.getDay());
};

/**
 * Converts JD to Gregorian date
 * @param {Number} jd JD
 * @return date in Gregorian calendar
 * @type YMD
 */
Calendar.jd_to_gregorian = function(jd) {
	var r = jd_to_gregorian(jd);
    return new YMD(r[0],r[1],r[2]);
};

/**
 * Converts Julian date to JD
 * @param {YMD} ymd
 * @return JD of given Julian date
 * @type Number
 */
Calendar.julian_to_jd = function(ymd) {
	return julian_to_jd(ymd.getYear(),ymd.getMonth(),ymd.getDay());
};

/**
 * Converts JD to Julian date
 * @param {Number} jd JD
 * @return date in Julian calendar
 * @type YMD
 */
Calendar.jd_to_julian = function(jd) {
	var r = jd_to_julian(jd);
    return new YMD(r[0],r[1],r[2],false,true);
};

/**
 * Converts Hebrew date to JD
 * @param {YMD} ymd
 * @return JD of given Hebrew date
 * @type Number
 */
Calendar.hebrew_to_jd = function(ymd)
{
	return hebrew_to_jd(ymd.getYear(),ymd.getMonth(),ymd.getDay());
};

/**
 * Converts JD to Hebrew date
 * @param {Number} jd JD
 * @return date in Hebrew calendar
 * @type YMD
 */
Calendar.jd_to_hebrew = function(jd)
{
	var r = jd_to_hebrew(jd);
    return new YMD(r[0],r[1],r[2],false,false,true);
};

Calendar.french_revolutionary_to_jd = function(an, mois, decade, jour)
{
	return french_revolutionary_to_jd(ymd.getYear(),ymd.getMonth(),ymd.getDay());
};

Calendar.jd_to_french_revolutionary = function(jd)
{
	var r = jd_to_french_revolutionary(jd);
    return new YMD(r[0],r[1],r[2],false,false,false,true);
};
