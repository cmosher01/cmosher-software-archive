/**
 * @fileoverview
 * Defines the {@link GedcomEvent} class.
 */

/**
 * @class Represents a GEDCOM event record.
 * @requires Util
 * 
 * @constructor
 * @param {String} typ type of this {@link GedcomEvent} (such as "birth" or "death")
 * @param {String} gdate GEDCOM date of this {@link GedcomEvent}.
 * @param {String} place place of this {@link GedcomEvent}.
 * @return new {@link GedcomEvent}
 * @type GedcomEvent
 */
function GedcomEvent(typ,gdate,place) {
	Util.verifyType(this,"GedcomEvent");

	/**
	 * type of event
	 * @private
	 * @type String
	 */
	this.typ = typ;

	/**
	 * date of this event
	 * @private
	 * @type String
	 */
	this.gdate = gdate;

	/**
	 * place of this event
	 * @private
	 * @type String
	 */
	this.place = place;
}

/**
 * Gets the type of this {@link GedcomEvent}.
 * @return type of event
 * @type String
 */
GedcomEvent.prototype.getType = function() {
	return this.typ;
};

/**
 * Gets the date of this {@link GedcomEvent}.
 * @return date of this {@link GedcomEvent}.
 * @type String
 */
GedcomEvent.prototype.getDate = function() {
	return this.gdate;
};

/**
 * Gets the place of this {@link GedcomEvent}.
 * @return place of this {@link GedcomEvent}.
 * @type String
 */
GedcomEvent.prototype.getPlace = function() {
	return this.place;
};
