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

Calendar.TropicalYear = 365.24219878;

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
    yearday = wjd - Calendar.gregorian_to_jd(new YMD(year, 1, 1));
    leapadj = ((wjd < Calendar.gregorian_to_jd(new YMD(year, 3, 1))) ? 0 :
                  (Calendar.leap_gregorian(year) ? 1 : 2)
              );
    month = Math.floor((((yearday + leapadj) * 12) + 373) / 367);
    day = (wjd - Calendar.gregorian_to_jd(new YMD(year, month, 1))) + 1;

    return new YMD(year, month, day);
};

/**
 * Checks if the given year is a leap year in the Julian calendar.
 * @param {Number} year
 * @return if the given year is a leap year in the Julian calendar
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

    return new YMD(year, month, day, false, true);
};

/**
 * Epoch for Gregorian calendar calculations
 * @private
 * @type Number
 */
Calendar.GREGORIAN_EPOCH = 1721425.5;













//HEBREW_TO_JD  --  Determine Julian day from Hebrew date

Calendar.HEBREW_EPOCH = 347995.5;

//  Is a given Hebrew year a leap year ?

Calendar.hebrew_leap = function(year)
{
    return Calendar.mod(((year * 7) + 1), 19) < 7;
};

//  How many months are there in a Hebrew year (12 = normal, 13 = leap)

Calendar.hebrew_year_months = function(year)
{
    return Calendar.hebrew_leap(year) ? 13 : 12;
};

//  Test for delay of start of new year and to avoid
//  Sunday, Wednesday, and Friday as start of the new year.

Calendar.hebrew_delay_1 = function(year)
{
    var months, day, parts;

    months = Math.floor(((235 * year) - 234) / 19);
    parts = 12084 + (13753 * months);
    day = (months * 29) + Math.floor(parts / 25920);

    if (Calendar.mod((3 * (day + 1)), 7) < 3) {
        day++;
    }
    return day;
};

//  Check for delay in start of new year due to length of adjacent years

Calendar.hebrew_delay_2 = function(year)
{
    var last, present, next;

    last = Calendar.hebrew_delay_1(year - 1);
    present = Calendar.hebrew_delay_1(year);
    next = Calendar.hebrew_delay_1(year + 1);

    return ((next - present) == 356) ? 2 :
                                     (((present - last) == 382) ? 1 : 0);
};

//  How many days are in a Hebrew year ?
Calendar.hebrew_year_days = function(year)
{
    return Calendar.hebrew_to_jd(new YMD(year + 1, 7, 1)) - Calendar.hebrew_to_jd(new YMD(year, 7, 1));
};

//  How many days are in a given month of a given year

Calendar.hebrew_month_days = function(year, month)
{
    //  First of all, dispose of fixed-length 29 day months

    if (month == 2 || month == 4 || month == 6 ||
        month == 10 || month == 13) {
        return 29;
    }

    //  If it's not a leap year, Adar has 29 days

    if (month == 12 && !Calendar.hebrew_leap(year)) {
        return 29;
    }

    //  If it's Heshvan, days depend on length of year

    if (month == 8 && !(Calendar.mod(Calendar.hebrew_year_days(year), 10) == 5)) {
        return 29;
    }

    //  Similarly, Kislev varies with the length of year

    if (month == 9 && (Calendar.mod(Calendar.hebrew_year_days(year), 10) == 3)) {
        return 29;
    }

    //  Nope, it's a 30 day month

    return 30;
};

//  Finally, wrap it all up into...

/**
 * Converts Hebrew date to JD
 * @param {YMD} ymd
 * @return JD of given Hebrew date
 * @type Number
 */Calendar.hebrew_to_jd = function(ymd)
{
    var jd, mon, months, year, month, day;
    year = ymd.getYear();
    month = ymd.getMonth();
    day = ymd.getDay();

    months = Calendar.hebrew_year_months(year);
    jd = Calendar.HEBREW_EPOCH + Calendar.hebrew_delay_1(year) +
    Calendar.hebrew_delay_2(year) + day + 1;

    if (month < 7) {
        for (mon = 7; mon <= months; mon++) {
            jd += Calendar.hebrew_month_days(year, mon);
        }
        for (mon = 1; mon < month; mon++) {
            jd += Calendar.hebrew_month_days(year, mon);
        }
    } else {
        for (mon = 7; mon < month; mon++) {
            jd += Calendar.hebrew_month_days(year, mon);
        }
    }

    return jd;
};

/*  JD_TO_HEBREW  --  Convert Julian date to Hebrew date
                      This works by making multiple calls to
                      the inverse function, and is this very
                      slow.  */

/**
 * Converts JD to Hebrew date
 * @param {Number} jd JD
 * @return date in Hebrew calendar
 * @type YMD
 */
Calendar.jd_to_hebrew = function(jd)
{
    var year, month, day, i, count, first;

    jd = Math.floor(jd) + 0.5;
    count = Math.floor(((jd - Calendar.HEBREW_EPOCH) * 98496.0) / 35975351.0);
    year = count - 1;
    for (i = count; jd >= Calendar.hebrew_to_jd(new YMD(i, 7, 1)); i++) {
        year++;
    }
    first = (jd < Calendar.hebrew_to_jd(new YMD(year, 1, 1))) ? 7 : 1;
    month = first;
    for (i = first; jd > Calendar.hebrew_to_jd(new YMD(year, i, Calendar.hebrew_month_days(year, i))); i++) {
        month++;
    }
    day = (jd - Calendar.hebrew_to_jd(new YMD(year, month, 1))) + 1;
    return new YMD(year, month, day);
};













/*  EQUINOX  --  Determine the Julian Ephemeris Day of an
equinox or solstice.  The "which" argument
selects the item to be computed:

   0   March equinox
   1   June solstice
   2   September equinox
   3   December solstice

*/

//Periodic terms to obtain true time

Calendar.EquinoxpTerms = new Array(
      485, 324.96,   1934.136,
      203, 337.23,  32964.467,
      199, 342.08,     20.186,
      182,  27.85, 445267.112,
      156,  73.14,  45036.886,
      136, 171.52,  22518.443,
       77, 222.54,  65928.934,
       74, 296.72,   3034.906,
       70, 243.58,   9037.513,
       58, 119.81,  33718.147,
       52, 297.17,    150.678,
       50,  21.02,   2281.226,
       45, 247.54,  29929.562,
       44, 325.15,  31555.956,
       29,  60.93,   4443.417,
       18, 155.12,  67555.328,
       17, 288.79,   4562.452,
       16, 198.04,  62894.029,
       14, 199.76,  31436.921,
       12,  95.39,  14577.848,
       12, 287.11,  31931.756,
       12, 320.81,  34777.259,
        9, 227.73,   1222.114,
        8,  15.45,  16859.074
            );

Calendar.JDE0tab1000 = new Array(
new Array(1721139.29189, 365242.13740,  0.06134,  0.00111, -0.00071),
new Array(1721233.25401, 365241.72562, -0.05323,  0.00907,  0.00025),
new Array(1721325.70455, 365242.49558, -0.11677, -0.00297,  0.00074),
new Array(1721414.39987, 365242.88257, -0.00769, -0.00933, -0.00006)
      );

Calendar.JDE0tab2000 = new Array(
new Array(2451623.80984, 365242.37404,  0.05169, -0.00411, -0.00057),
new Array(2451716.56767, 365241.62603,  0.00325,  0.00888, -0.00030),
new Array(2451810.21715, 365242.01767, -0.11575,  0.00337,  0.00078),
new Array(2451900.05952, 365242.74049, -0.06223, -0.00823,  0.00032)
      );

Calendar.equinox = function(year, which)
{
	var deltaL, i, j, JDE0, JDE, JDE0tab, S, T, W, Y;
	
	/*  Initialise terms for mean equinox and solstices.  We
	have two sets: one for years prior to 1000 and a second
	for subsequent years.  */
	
	if (year < 1000) {
		JDE0tab = Calendar.JDE0tab1000;
		Y = year / 1000;
	} else {
		JDE0tab = Calendar.JDE0tab2000;
		Y = (year - 2000) / 1000;
	}
	
	JDE0 =  JDE0tab[which][0] +
	(JDE0tab[which][1] * Y) +
	(JDE0tab[which][2] * Y * Y) +
	(JDE0tab[which][3] * Y * Y * Y) +
	(JDE0tab[which][4] * Y * Y * Y * Y);
	
	//document.debug.log.value += "JDE0 = " + JDE0 + "\n";
	
	T = (JDE0 - 2451545.0) / 36525;
	//document.debug.log.value += "T = " + T + "\n";
	W = (35999.373 * T) - 2.47;
	//document.debug.log.value += "W = " + W + "\n";
	deltaL = 1 + (0.0334 * Calendar.dcos(W)) + (0.0007 * dcos(2 * W));
	//document.debug.log.value += "deltaL = " + deltaL + "\n";
	
	//  Sum the periodic terms for time T
	
	S = 0;
	for (i = j = 0; i < 24; i++) {
		S += Calendar.EquinoxpTerms[j] * Calendar.dcos(Calendar.EquinoxpTerms[j + 1] + (Calendar.EquinoxpTerms[j + 2] * T));
		j += 3;
	}
	
	//document.debug.log.value += "S = " + S + "\n";
	//document.debug.log.value += "Corr = " + ((S * 0.00001) / deltaL) + "\n";
	
	JDE = JDE0 + ((S * 0.00001) / deltaL);
	
	return JDE;
};






















/*  EQUINOXE_A_PARIS  --  Determine Julian day and fraction of the
September equinox at the Paris meridian in
a given Gregorian year.  */

Calendar.equinoxe_a_paris = function(year)
{
	var equJED, equJD, equAPP, equParis, dtParis;
	
	//  September equinox in dynamical time
	equJED = Calendar.equinox(year, 2);
	
	//  Correct for delta T to obtain Universal time
	equJD = equJED - (Calendar.deltat(year) / (24 * 60 * 60));
	
	//  Apply the equation of time to yield the apparent time at Greenwich
	equAPP = equJD + Calendar.equationOfTime(equJED);
	
	/*  Finally, we must correct for the constant difference between
	the Greenwich meridian and that of Paris, 2°20'15" to the
	East.  */
	
	dtParis = (2 + (20 / 60.0) + (15 / (60 * 60.0))) / 360;
	equParis = equAPP + dtParis;
	
	return equParis;
};

/*  PARIS_EQUINOXE_JD  --  Calculate Julian day during which the
 September equinox, reckoned from the Paris
 meridian, occurred for a given Gregorian
 year.  */

Calendar.paris_equinoxe_jd = function(year)
{
	var ep, epg;
	
	ep = Calendar.equinoxe_a_paris(year);
	epg = Math.floor(ep - 0.5) + 0.5;
	
	return epg;
};

/*  ANNEE_DE_LA_REVOLUTION  --  Determine the year in the French
revolutionary calendar in which a
given Julian day falls.  Returns an
array of two elements:

    [0]  Année de la Révolution
    [1]  Julian day number containing
         equinox for this year.
*/

Calendar.FRENCH_REVOLUTIONARY_EPOCH = 2375839.5;

Calendar.annee_da_la_revolution = function(jd)
{
	var guess = Calendar.jd_to_gregorian(jd)[0] - 2,
	lasteq, nexteq, adr;
	
	lasteq = Calendar.paris_equinoxe_jd(guess);
	while (lasteq > jd) {
		guess--;
		lasteq = Calendar.paris_equinoxe_jd(guess);
	}
	nexteq = lasteq - 1;
	while (!((lasteq <= jd) && (jd < nexteq))) {
		lasteq = nexteq;
		guess++;
		nexteq = Calendar.paris_equinoxe_jd(guess);
	}
	adr = Math.round((lasteq - Calendar.FRENCH_REVOLUTIONARY_EPOCH) / Calendar.TropicalYear) + 1;
	
	return new Array(adr, lasteq);
};

/*  JD_TO_FRENCH_REVOLUTIONARY  --  Calculate date in the French Revolutionary
    calendar from Julian day.  The five or six
    "sansculottides" are considered a thirteenth
    month in the results of this function.  */

Calendar.jd_to_french_revolutionary = function(jd)
{
	var an, mois, decade, jour,
	adr, equinoxe;
	
	jd = Math.floor(jd) + 0.5;
	adr = Calendar.annee_da_la_revolution(jd);
	an = adr[0];
	equinoxe = adr[1];
	mois = Math.floor((jd - equinoxe) / 30) + 1;
	jour = (jd - equinoxe) % 30;
	decade = Math.floor(jour / 10) + 1;
	jour = (jour % 10) + 1;
	
	return new Array(an, mois, decade, jour);
};

/*  FRENCH_REVOLUTIONARY_TO_JD  --  Obtain Julian day from a given French
    Revolutionary calendar date.  */

Calendar.french_revolutionary_to_jd = function(an, mois, decade, jour)
{
	var adr, equinoxe, guess, jd;
	
	guess = Calendar.FRENCH_REVOLUTIONARY_EPOCH + (Calendar.TropicalYear * ((an - 1) - 1));
	adr = new Array(an - 1, 0);
	
	while (adr[0] < an) {
		adr = Calendar.annee_da_la_revolution(guess);
		guess = adr[1] + (Calendar.TropicalYear + 2);
	}
	equinoxe = adr[1];
	
	jd = equinoxe + (30 * (mois - 1)) + (10 * (decade - 1)) + (jour - 1);
	return jd;
};









/**
 * @private
 * @param {Number} a
 * @param {Number} b
 * @return mod
 * @type Number
 */
Calendar.mod = function(a, b) {
    return a - (b * Math.floor(a / b));
};
Calendar.dtr = function(d) {
    return (d * Math.PI) / 180.0;
};
Calendar.dcos = function(d) {
    return Math.cos(Calendar.dtr(d));
};
