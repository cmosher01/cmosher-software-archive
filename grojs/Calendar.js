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
 * Checks if the given year is a leap year in the Gregorian calendar.
 * @param {Number} year
 * @return <code>true</code> if leap year
 * @type Boolean
 */
Calendar.leap_gregorian = function(year)
{
    return (Calendar.mod(year,4) == 0) &&
            (!((Calendar.mod(year,100) == 0) && (Calendar.mod(year,400) != 0)));
};

/**
 * Converts Gregorian date to JD
 * @param {YMD} ymd
 * @return JD of given Gregorian date
 * @type Number
 */
Calendar.gregorian_to_jd = function(ymd) {
	var year = ymd.getYear();
	var month = ymd.getMonth();
	var day = ymd.getDay();
    return (Calendar.GREGORIAN_EPOCH - 1) +
	    (365 * (year - 1)) +
	    Math.floor((year - 1) / 4) +
	    (-Math.floor((year - 1) / 100)) +
	    Math.floor((year - 1) / 400) +
	    Math.floor((((367 * month) - 362) / 12) +
	        ((month <= 2) ? 0 : (Calendar.leap_gregorian(year) ? -1 : -2)) + day);
};

/**
 * Converts JD to Gregorian date
 * @param {Number} jd JD
 * @return date in Gregorian calendar
 * @type YMD
 */
Calendar.jd_to_gregorian = function(jd) {
    var wjd, depoch, quadricent, dqc, cent, dcent, quad, dquad,
        yindex, year, month, day, yearday, leapadj;

    wjd = Math.floor(jd - 0.5) + 0.5;
    depoch = wjd - Calendar.GREGORIAN_EPOCH;
    quadricent = Math.floor(depoch / 146097);
    dqc = Calendar.mod(depoch,146097);
    cent = Math.floor(dqc / 36524);
    dcent = Calendar.mod(dqc,36524);
    quad = Math.floor(dcent / 1461);
    dquad = Calendar.mod(dcent,1461);
    yindex = Math.floor(dquad / 365);
    year = (quadricent * 400) + (cent * 100) + (quad * 4) + yindex;
    if (!((cent == 4) || (yindex == 4))) {
        year++;
    }
    yearday = wjd - Calendar.gregorian_to_jd(year, 1, 1);
    leapadj = ((wjd < Calendar.gregorian_to_jd(year, 3, 1)) ? 0 :
                  (Calendar.leap_gregorian(year) ? 1 : 2)
              );
    month = Math.floor((((yearday + leapadj) * 12) + 373) / 367);
    day = (wjd - Calendar.gregorian_to_jd(year, month, 1)) + 1;

    return new YMD(year, month, day);
};

/**
 * Checks if the given year is a leap year in the Julian calendar.
 * @param {Number} year
 * @return 
 * @type Boolean
 */
Calendar.leap_julian = function(year) {
    return Calendar.mod(year,4) == ((year > 0) ? 0 : 3);
};

/**
 * Converts Julian date to JD
 * @param {YMD} ymd
 * @return JD of given Julian date
 * @type Number
 */
Calendar.julian_to_jd = function(ymd) {
	var year = ymd.getYear();
	var month = ymd.getMonth();
	var day = ymd.getDay();

    /* Adjust negative common era years to the zero-based notation we use.  */

    if (year < 1) {
        year++;
    }

    /* Algorithm as given in Meeus, Astronomical Algorithms, Chapter 7, page 61 */

    if (month <= 2) {
        year--;
        month += 12;
    }

    return ((Math.floor((365.25 * (year + 4716))) +
            Math.floor((30.6001 * (month + 1))) +
            day) - 1524.5);
};

/**
 * Converts JD to Julian date
 * @param {Number} jd JD
 * @return date in Julian calendar
 * @type YMD
 */
Calendar.jd_to_julian = function(jd) {
    var z, a, b, c, d, e, year, month, day;

    jd += 0.5;
    z = Math.floor(jd);

    a = z;
    b = a + 1524;
    c = Math.floor((b - 122.1) / 365.25);
    d = Math.floor(365.25 * c);
    e = Math.floor((b - d) / 30.6001);

    month = Math.floor((e < 14) ? (e - 1) : (e - 13));
    year = Math.floor((month > 2) ? (c - 4716) : (c - 4715));
    day = b - d - Math.floor(30.6001 * e);

    /*  If year is less than 1, subtract one to convert from
        a zero based date system to the common era system in
        which the year -1 (1 B.C.E) is followed by year 1 (1 C.E.).  */

    if (year < 1) {
        year--;
    }

    return new YMD(year, month, day);
};

/**
 * Epoch for Gregorian calendar calculations
 * @private
 * @type Number
 */
Calendar.GREGORIAN_EPOCH = 1721425.5;

/**
 * @private
 * @param {Number} a
 * @param {Number} b
 * @return mod
 * @type Number
 */
Calendar.mod = function(a, b)
{
    return a - (b * Math.floor(a / b));
};
