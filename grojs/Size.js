/**
 * @class
 * Represents the size of a graphical object in 2D space.
 * @constructor
 * @param {Number} w width
 * @param {Number} h height
 * @return new {@link Size}
 * @type Size
 */
function Size(w,h) {
	Util.verifyType(this,"Size");
	/**
	 * width
	 * @private
	 * @type Number
	 */
	this.w = parseInt(w);
	/**
	 * height
	 * @private
	 * @type Number
	 */
	this.h = parseInt(h);
}

/**
 * Gets the width of this {@link Size}.
 * @returns width
 * @type Number
 */
Size.prototype.getWidth = function() {
	return this.w;
};

/**
 * Gets the height of this {@link Size}.
 * @returns width
 * @type Number
 */
Size.prototype.getHeight = function() {
	return this.h;
};

/**
 * Gets a debug string for this {@link Size}.
 * @return debug string
 * @type String
 */
Size.prototype.toString = function() {
	return "WxH="+this.getWidth()+"x"+this.getHeight();
};
