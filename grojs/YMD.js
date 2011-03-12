/**
 * @fileoverview
 * Defines the {@link YMD} (year/month/day) class.
 */

/**
 * @class
 * Represents a date, specified as a year, month, and day, allowing
 * for unknown month and/or day. An unknown day or month is specified as zero.
 * @requires Util
 * 
 * @constructor
 * @param {Number} y year -9999 to -1 (for BC), or 1 to 9999 (for AD)
 * @param {Number} m month 1-12 (0 or omitted means unknown)
 * @param {Number} d day 1-31 (0 or omitted means unknown)
 * @param {Boolean} circa
 * @param {Boolean} julian
 * @return new {@link YMD}
 * @type YMD
 */
function YMD(y,m,d,circa,julian) {
	Util.verifyType(this,"YMD");

	/**
	 * year (-9999 to -1, or 1 to 9999)
	 * @private
	 * @type Number
	 */
	this.year = parseInt(y,10);
	if (this.year <= -10000 || this.year == 0 || 10000 <= this.year) {
		throw new Error("invalid year: "+this.year);
	}

	/**
	 * month (1 = Jan) (0 = unknown)
	 * @private
	 * @type Number
	 */
	this.month = !m ? 0 : parseInt(m,10);
	if (this.month < 0 || 12 < this.month) {
		throw new Error("invalid month: "+this.month);
	}

	/**
	 * day of month (1-31) (0 = unknown)
	 * @private
	 * @type Number
	 */
	this.day = !d ? 0 : parseInt(d,10);
	if (this.day < 0 || 31 < this.day) {
		throw new Error("invalid day: "+this.day);
	}

	/**
	 * if this date is an approximation
	 * @private
	 * @type Boolean
	 */
	this.circa = !!circa;

	/**
	 * if this date SHOULD be DISPLAYED in Julian Calendar
	 * @private
	 * @type Boolean
	 */
	this.julian = !!julian;

	/**
	 * @private
	 * @type Date
	 */
	this.approx = this.calcApprox();
}

/**
 * Gets the day
 * @return the day, or zero if unknown
 * @type Number
 */
YMD.prototype.getDay = function() {
    return this.day;
};

/**
 * Gets the month
 * @return the month (1 means January), or zero if unknown
 * @type Number
 */
YMD.prototype.getMonth = function() {
    return this.month;
};

/**
 * Gets the year
 * @return the year, or zero if unknown. (negative means BC)
 * @type Number
 */
YMD.prototype.getYear = function() {
    return this.year;
};

/**
 * @return if this date should be show using the Julian calendar
 * (if so, the caller is responsible for converting it).
 * @type Boolean
 */
YMD.prototype.isJulian = function() {
	return this.julian;
};

/**
 * @return if this is an approximate date
 * @type Boolean
 */
YMD.prototype.isCirca = function() {
	return this.circa;
};

/**
 * Gets the exact <code>Date</code> represented by this {@link YMD},
 * assuming it is exact. Throws otherwise.
 * @return the <code>Date</code> representing this exact {@link YMD} (at noon, local time).
 * @type Date
 * @throws if this {@link YMD} is missing day or month
 */
YMD.prototype.getExactDate = function() {
	if (!this.isExact()) {
		throw new Error("missing month or day on date that was presumed to be exact");
	}

	return this.approx;
};

/**
 * Gets a <code>Date</code> that can be used as an approximation
 * of this {@link YMD} for computation purposes.
 * Never display this value to the user!
 * @return an approximate <code>Date</code> for this {@link YMD}
 * @type Date
 */
YMD.prototype.getApproxDate = function() {
	return this.approx;
};

/**
 * Gets if this {@link YMD} is exact.
 * @return <code>true</code> if exact
 * @type Boolean
 */
YMD.prototype.isExact = function() {
	return YMD.valid(this.month) && YMD.valid(this.day);
};

/**
 * Returns a new {@link YMD} representing January 1, 9999 BC.
 * @return Jan. 1, 9999 BC
 * @type YMD
 */
YMD.getMinimum = function() {
	return new YMD(-9999,1,1);
};

/**
 * Returns a new {@link YMD} representing December 31, AD 9999.
 * @return Dec. 31, AD 9999
 * @type YMD
 */
YMD.getMaximum = function() {
	return new YMD(9999,12,31);
};



/**
 * @return date in yyyy-mm-dd format
 * @type String
 */
YMD.prototype.toString = function() {
	var s;
	s = "";

	if (this.circa) {
		s += "c. ";
	}

	if (this.year == 9999 || this.year == -9999) {
		if (YMD.equal(this,YMD.getMaximum())) {
			return "(MAX)";
		}
		if (YMD.equal(this,YMD.getMinimum())) {
			return "(MIN)";
		}
	}
	s += Util.digint(this.year,4);
	if (this.month > 0) {
		s += "-"+Util.digint(this.month,2);
		if (this.day > 0) {
			s += "-"+Util.digint(this.day,2);
		}
	}
	return s;
};

/**
 * @private
 * @param {Number} i
 * @return if given number is not zero (not unknown)
 * @type Boolean
 */
YMD.valid = function(i) {
	return i != 0;
};

/**
 * Approximate date. Never display this to the user; only use
 * it for approximation in calculations (for example, for sorting).
 * @private
 * @return approximate date (useful if month or day are unknown)
 * @type Date
 */
YMD.prototype.calcApprox = function() {
	var dt;
	var m = this.month;
	var d = this.day;

	// if month and day are missing, assume mid-year (July 3).
	if (m == 0 && d == 0) {
		m = 7;
		d = 3;
	}
	// if just day is missing, assume mid-month (the 15th).
	else if (d == 0) {
		d = 15;
	}

	dt = new Date(0);
	dt.setUTCFullYear(this.year);
	dt.setUTCMonth(m-1);
	dt.setUTCDate(d);
	return dt;
};

/**
 * Checks two {@link YMD}s for equality.
 * @param {YMD} a not null/undefined
 * @param {YMD} b not null/undefined
 * @return if a and b are equal
 * @type Boolean
 */
YMD.equal = function(a,b) {
	return a.year==b.year && a.month==b.month && a.day==b.day;
};


/**
 * Compares two {@link YMD}s, for sorting.
 * @param {YMD} a not null/undefined
 * @param {YMD} b not null/undefined
 * @return -1:a&lt;b, 0:a=b, +1:a&gt;b
 * @type Number
 */
YMD.order = function(a,b) {
	return Util.dateOrder(a.getApproxDate(),b.getApproxDate());
};

/**
 * 
 * @param {Object} r
 * @return if parser result is a YMD type
 * @type Boolean
 */
YMD.isParsedYMD = function(r) {
	if (!r) {
		return false;
	}
	return r.hasOwnProperty("year") && r.hasOwnProperty("month") && r.hasOwnProperty("day");
};

/**
 * 
 * @param {Object} r result from parser
 * @return new {@link YMD} (or null if !r)
 * @type YMD
 */
YMD.fromParserResult = function(r) {
	if (!r) {
		return null;
	}
	return new YMD(r.year,r.month,r.day,r.approx,r.julian);
};
