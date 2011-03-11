/**
 * @fileoverview
 * Defines the {@link DateRange} class.
 */

/**
 * @class
 * Represents a range of possible dates.
 * @requires Util
 * @requires YMD
 * 
 * @constructor
 * @param {YMD} earliest 
 * @param {YMD} latest 
 * @return new {@link DateRange}
 * @type DateRange
 */
function DateRange(earliest, latest) {
	Util.verifyType(this,"DateRange");

	this.earliest = earliest;
	if (!this.earliest) {
		throw new Error("missing date");
	}
	this.latest = latest;
	if (!this.latest) {
		this.latest = this.earliest;
	}

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
	return YMD.equal(this.earliest,this.latest);
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
	if (this.isExact()) {
		return this.earliest.toString();
	}
	return this.earliest.toString()+"?"+this.latest.toString();
};

/**
 * @private
 * @return an approximation of this range
 * @type Date
 */
DateRange.prototype.calcApprox = function() {
	return new Date((this.earliest.getApproxDate().getMilliseconds()+this.latest.getApproxDate().getMilliseconds())/2);
};

/**
 * Checks two {@link DateRange}s for equality.
 * @param {DateRange} a
 * @param {DateRange} b
 * @return if a and b are equal
 * @type Boolean
 */
DateRange.equal = function(a,b) {
	return YMD.equal(a.getEarliest(),b.getEarliest()) && YMD.equal(a.getLatest(),b.getLatest());
};

/**
 * Compares two {@link DateRange}s, for sorting.
 * @param {DateRange} a
 * @param {DateRange} b
 * @return -1:a<b, 0:a=b, +1:a>b
 * @type Number
 */
DateRange.order = function(a,b) {
	YMD.order(a,b);
};
