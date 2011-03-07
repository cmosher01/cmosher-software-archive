/**
 * @class Represents a graphical rectangle in 2D space.
 * @constructor
 * @param {Point} pos the location of the top-left corner of this {@link Rect}
 * @param {Size} siz the width and height of this {@link Rect}
 * @return new {@link Rect}
 * @type Rect
 */
function Rect(pos,siz) {
	Util.verifyType(this,"Rect");

	/**
	 * position of top-left corner
	 * @private
	 * @type Point
	 */
	this.pos = pos;
	Util.verifyType(this.pos,"Point");

	/**
	 * width and height
	 * @private
	 * @type Size
	 */
	this.siz = siz;
	Util.verifyType(this.siz,"Size");
}

/**
 * Gets the location of the top-left corner of this {@link Rect}
 * @return top-left
 * @type Point
 */
Rect.prototype.getPos = function() {
	return this.pos;
};

/**
 * Gets the width and height of this {@link Rect}
 * @return width and height
 * @type Size
 */
Rect.prototype.getSize = function() {
	return this.siz;
};

/**
 * Gets the x coordinate of the left of this {@link Rect}.
 * @return left
 * @type Number
 */
Rect.prototype.getLeft = function() {
	return this.pos.getX();
};

/**
 * Gets the width of this {@link Rect}
 * @return width
 * @type Number
 */
Rect.prototype.getWidth = function() {
	return this.siz.getWidth();
};

/**
 * Gets the y coordinate of the top of this {@link Rect}.
 * @return top
 * @type Number
 */
Rect.prototype.getTop = function() {
	return this.pos.getY();
};

/**
 * Gets the height of this {@link Rect}
 * @return height
 * @type Number
 */
Rect.prototype.getHeight = function() {
	return this.siz.getHeight();
};

/**
 * Gets the x coordinate of the right of this {@link Rect}.
 * @return right
 * @type Number
 */
Rect.prototype.getRight = function() {
	return this.getLeft()+this.getWidth();
};

/**
 * Gets the y coordinate of the bottom of this {@link Rect}.
 * @return bottom
 * @type Number
 */
Rect.prototype.getBottom = function() {
	return this.getTop()+this.getHeight();
};

/**
 * Gets the x coordinate of the middle of this {@link Rect}.
 * @return mid-x
 * @type Number
 */
Rect.prototype.getMidX = function() {
	return Math.round((this.getLeft()+this.getRight())/2);
};

/**
 * Gets the y coordinate of the middle of this {@link Rect}.
 * @return mid-y
 * @type Number
 */
Rect.prototype.getMidY = function() {
	return Math.round((this.getTop()+this.getBottom())/2);
};

/**
 * Gets the given HTMLElement's location (offset).
 * @param {HTMLElement} e element to get the location of
 * @return new {@link Rect} describing the given HTMLElement's location.
 * @type Rect
 */
Rect.ofDiv = function(e) {
	return new Rect(new Point(e.offsetLeft,e.offsetTop),new Size(e.offsetWidth,e.offsetHeight));
};
