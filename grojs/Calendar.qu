$(function() {

	module("Calendar");

	test("nominal George Washington's birthday: Julian to Gregorian", function() {
		var ymdJulian;
		var ymdGregorian;
		ymdJulian = new YMD(1732,2,11,false,true);
		ymdGregorian = Calendar.jd_to_gregorian(Calendar.julian_to_jd(ymdJulian));

		deepEqual(ymdGregorian,new YMD(1732,2,22,false,false));
	});

	test("nominal Gregorian to Julian", function() {
		var ymdGregorian;
		var ymdJulian;
		ymdGregorian = new YMD(1752,9,14,false,false);
		ymdJulian = Calendar.jd_to_julian(Calendar.gregorian_to_jd(ymdGregorian));

		deepEqual(ymdJulian,new YMD(1752,9,3,false,true));
	});

	test("nominal Hebrew to Gregorian", function() {
		var ymdHebrew;
		var ymdGregorian;
		ymdHebrew = new YMD(5771,7,1);
		ymdGregorian = Calendar.jd_to_gregorian(Calendar.hebrew_to_jd(ymdHebrew));
		deepEqual(ymdGregorian,new YMD(2010,9,9));
	})

	test("nominal Gregorian to Hebrew", function() {
		var ymdGregorian;
		var ymdHebrew;
		ymdGregorian = new YMD(2010,9,9);
		ymdHebrew = Calendar.jd_to_hebrew(Calendar.gregorian_to_jd(ymdGregorian));
		deepEqual(ymdHebrew,new YMD(5771,7,1));
	})

	test("nominal French Rev. to Gregorian", function() {
		var ymdFrench;
		var ymdGregorian;
		ymdFrench = new YMD(2,11,9);
		ymdGregorian = Calendar.jd_to_gregorian(Calendar.french_revolutionary_to_jd(ymdFrench));
		deepEqual(ymdGregorian,new YMD(1794,7,27));
	})

	test("nominal Gregorian to French Rev.", function() {
		var ymdGregorian;
		var ymdFrench;
		ymdGregorian = new YMD(1794,7,27);
		ymdFrench = Calendar.jd_to_french_revolutionary(Calendar.gregorian_to_jd(ymdGregorian));
		deepEqual(ymdFrench,new YMD(2,11,9));
	})
});
