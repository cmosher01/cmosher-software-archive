/**
 * @fileoverview
 * Defines the {@link Point} class.
 */

/**
 * @class Represents a graphical point in 2D space.
 * @requires Util
 * @constructor
 * @param {Number} x x-coordinate of this {@link Point}
 * @param {Number} y y-coordinate of this {@link Point}
 * @return new {@link Point}
 * @type Point
 */
function Point(x,y) {
	Util.verifyType(this,"Point");

	/**
	 * x-coordinate
	 * @private
	 * @type Number
	 */
	this.x = parseInt(x);

	/**
	 * y-coordinate
	 * @private
	 * @type Number
	 */
	this.y = parseInt(y);
}

/**
 * Gets the x coordinate of this {@link Point}
 * @return the x coordinate
 * @type Number
 */
Point.prototype.getX = function() {
	return this.x;
};

/**
 * Gets the y coordinate of this {@link Point}
 * @return the y coordinate
 * @type Number
 */
Point.prototype.getY = function() {
	return this.y;
};

/**
 * Gets a debug string for this {@link Point}
 * @return {String} debug string
 * @type String
 */
Point.prototype.toString = function() {
	return "("+this.getX()+","+this.getY()+")";
};
