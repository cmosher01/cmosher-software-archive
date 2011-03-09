/**
 * @fileoverview
 * Defines the {@link DatePeriod} class.
 */

/**
 * @class
 * Represents a range of possible dates.
 * @requires Util
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
		this.dateEnd = this.dateStart
	}
}

/**
 * @return the start date
 * @type DateRange
 */
DatePeriod.prototype.getStartDate = function() {
	return this.dateStart;
}

/**
 * @return the end date
 * @type DateRange
 */
DatePeriod.prototype.getEndDate = function() {
	return this.dateEnd;
}

/**
 * @return date period string
 * @type String
 */
DatePeriod.prototype.toString = function() {
	if (this.dateStart === this.dateEnd) {
		return this.dateStart.toString();
	}
	return this.dateStart+"-"+this.dateEnd;
}

/**
 * Checks to see if this DatePeriod overlaps the given DatePeriod.
 * @param periodTarget
 * @return true if they overlap
public boolean overlaps(final DatePeriod periodTarget)
{
	return this.dateStart.compareTo(periodTarget.dateEnd) <= 0 && periodTarget.dateStart.compareTo(this.dateEnd) <= 0;
};
 */
