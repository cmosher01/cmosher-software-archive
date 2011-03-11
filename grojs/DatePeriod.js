/**
 * @fileoverview
 * Defines the {@link DatePeriod} class.
 */

/**
 * @class
 * Represents a period of time.
 * @requires Util
 * @requires DateRange
 * 
 * @constructor
 * @param {DateRange} dateStart
 * @param {DateRange} dateEnd
 * @return new {@link DatePeriod}
 * @type DatePeriod
 */
function DatePeriod(dateStart, dateEnd) {
	Util.verifyType(this,"DatePeriod");
	this.dateStart = dateStart;
	this.dateEnd = dateEnd;
	if (!this.dateEnd) {
		this.dateEnd = this.dateStart;
	}
}

/**
 * @return the start date
 * @type DateRange
 */
DatePeriod.prototype.getStartDate = function() {
	return this.dateStart;
};

/**
 * @return the end date
 * @type DateRange
 */
DatePeriod.prototype.getEndDate = function() {
	return this.dateEnd;
};

/**
 * @return if start == end
 * @type Boolean
 */
DatePeriod.prototype.isSingle = function() {
	return DateRange.equal(this.dateStart,this.dateEnd);
};

/**
 * @return date period string
 * @type String
 */
DatePeriod.prototype.toString = function() {
	if (this.isSingle()) {
		return this.dateStart.toString();
	}
	return this.dateStart.toString()+"-"+this.dateEnd.toString();
};

/**
 * Checks two {@link DatePeriod}s for equality.
 * @param {DatePeriod} a
 * @param {DatePeriod} b
 * @return if a and b are equal
 * @type Boolean
 */
DatePeriod.equal = function(a,b) {
	return DateRange.equal(a.getStartDate(),b.getStartDate()) && DateRange.equal(a.getEndDate(),b.getEndDate());
};

/**
 * Compares two {@link DatePeriod}s, for sorting.
 * @param {DatePeriod} a
 * @param {DatePeriod} b
 * @return -1:a<b, 0:a=b, +1:a>b
 * @type Number
 */
DatePeriod.order = function(a,b) {
	var c;
	c = DateRange.order(a.getStartDate(),b.getStartDate());
	if (!c) {
		c = DateRange.order(a.getEndDate(),b.getEndDate());
	}
	return c;
};

/**
 * Checks if two {@link DatePeriod}s overlap.
 * @param {DatePeriod} a
 * @param {DatePeriod} b
 * @return if they overlap
 * @type Boolean
 */
DatePeriod.overlap = function(a,b) {
	return DateRange.order(a.getStartDate(),b.getEndDate()) < 0 && DateRange.order(b.getStartDate(),a.getEndDate()) < 0;
};
