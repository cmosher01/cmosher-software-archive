/**
 * @fileoverview
 * Defines the {@link DateRange} class.
 */

/**
 * @class
 * Represents a range of possible dates.
 * @requires Util
 * 
 * @constructor
 * @return new {@link DateRange}
 * @type DateRange
 */
function DateRange(earliest, latest, circa, julian) {
	Util.verifyType(this,"DateRange");

	this.earliest = earliest;
	if (!this.earliest) {
		throw new Error("missing date");
	}
	this.latest = latest;
	if (!this.latest) {
		this.latest = this.earliest;
	}

	this.circa = !!circa;
	this.julian = !!julian;

	this.approx = this.calcApprox();
}



/**
 * @return earliest possible date
 * @type YMD
 */
DateRange.prototype.getEarliest = function() {
	return this.earliest;
};

/**
 * @return latest possible date
 * @type YMD
 */
DateRange.prototype.getLatest = function() {
	return this.latest;
};

/**
 * @return if this represents an exact date
 * @type Boolean
 */
DateRange.prototype.isExact = function() {
	return this.earliest === this.latest;
};

/**
 * @return if this date should be show using the Julian calendar
 * (if so, the caller is responsible for converting it).
 * @type Boolean
 */
DateRange.prototype.isJulian = function() {
	return this.julian;
};

/**
 * @return if this is an approximate date
 * @type Boolean
 */
DateRange.prototype.isCirca = function() {
	return this.circa;
};

/**
 * @return an approximation of this range
 * @type Date
 */
DateRange.prototype.getApproxDay = function() {
	return this.approx;
};

/**
 * @return date string
 * @type String
 */
DateRange.prototype.toString = function() {
	var s;
	s = "";
	if (this.circa) {
		s += "c. ";
	}

	if (this.isExact()) {
		s += this.earliest.toString();
	} else {
		s += this.earliest.toString();
		s += "?";
		s += this.latest.toString();
	}
	return s;
};

/**
 * @private
 * @return an approximation of this range
 * @type Date
 */
DateRange.prototype.calcApprox = function() {
	return new Date((this.earliest.getApproxDate().getMilliseconds()+this.latest.getApproxDate().getMilliseconds())/2);
};
