/**
 * @fileoverview
 * Defines the {@link GedcomLine} class.
 */

/**
 * @class Represents one line of a GEDCOM file.
 * @requires Util
 * 
 * @constructor
 * @param {Number} level the level of this {@link GedcomLine}.
 * @param {String} gid the ID of this {@link GedcomLine}. Optional.
 * @param {String} tag the tag of this {@link GedcomLine}.
 * @param {String} val the value of this {@link GedcomLine}. Optional
 * @return new {@link GedcomLine}
 * @type GedcomLine
 */
function GedcomLine(level, gid, tag, val) {
	Util.verifyType(this,"GedcomLine");

	/**
	 * Level number. Greater than or equal to zero.
	 * @private
	 * @type Number
	 */
	this.level = parseInt(level,10);

	/**
	 * ID of this line, if it has one, otherwise empty string.
	 * @private
	 * @type String
	 */
	this.gid = GedcomLine.asPointer(gid);

	/**
	 * GEDCOM tag of this line (not validated).
	 * @private
	 * @type String
	 */
	this.tag = Util.safeStr(tag).toUpperCase();

	/**
	 * ID of other line if value is a pointer, otherwise empty string.
	 * @private
	 * @type String
	 */
	this.pointer = GedcomLine.asPointer(val);

	if (this.pointer) {
		/**
		 * Value of this line, or empty string.
		 * @private
		 * @type String
		 */
		this.val = Util.safeStr();
	} else {
		this.val = GedcomLine.replaceAts(Util.safeStr(val));
	}
}

/**
 * Gets the level number of this {@link GedcomLine}.
 * @return level
 * @type Number
 */
GedcomLine.prototype.getLevel = function() {
	return this.level;
};

/**
 * Gets the ID of this {@link GedcomLine}, or empty string if this
 * line doesn't have an ID.
 * @return ID
 * @type String
 */
GedcomLine.prototype.getID = function() {
	return this.gid;
};

/**
 * Gets the GECOM tag of this {@link GedcomLine}.
 * @return tag
 * @type String
 */
GedcomLine.prototype.getTag = function() {
	return this.tag;
};

/**
 * Gets the value of this {@link GedcomLine}. May be empty.
 * @return value
 * @type String
 */
GedcomLine.prototype.getVal = function() {
	return this.val;
};

/**
 * If the value of this {@link GedcomLine} is just a pointer to
 * another line, then this method gets the ID of that other line.
 * Otherwise, returns an empty string.
 * @return pointer
 * @type String
 */
GedcomLine.prototype.getPointer = function() {
	return this.pointer;
};

/**
 * Returns <code>true</code> if this {@link GedcomLine} has an ID.
 * @return if this {@link GedcomLine} has an ID
 * @type Boolean
 */
GedcomLine.prototype.hasID = function() {
	return !!this.getID();
};

/**
 * Returns <code>true</code> if this {@link GedcomLine}'s value
 * is just a pointer to another line.
 * @return if this {@link GedcomLine} is a pointer
 * @type Boolean
 */
GedcomLine.prototype.isPointer = function() {
	return !!this.getPointer();
};

/**
 * Joins a CONC or CONT {@link GedcomLine} to this {@link GedcomLine}.
 * If the given {@link GedcomLine} is not a CONC or CONT line, then do nothing.
 * @param {GedcomLine} c the CONC or CONT line
 */
GedcomLine.prototype.concat = function(c) {
	switch (c.getTag()) {
		case "CONC": this.val += c.getVal(); break;
		case "CONT": this.val += "\n"+c.getVal(); break;
	}
};

/**
 * Creates a {@link GedcomLine} from parsing the given GEDCOM line string.
 * @param {String} s the GEDCOM line to be parsed.
 * @returns new {@link GedcomLine} from parsing s
 * @type GedcomLine
 */
GedcomLine.parse = function(s) {
	var r = /^\s*(\d+)\s+(?:(@[^@]+@)\s+)?(\S+)(?:\s(.*))?$/.exec(s);
	if (r == null) {
		throw new Error("Gedcom line has invalid syntax: "+s);
	}
	return new GedcomLine(r[1],r[2],r[3],r[4]);
};

/**
 * Parses the given string to see if it is a GEDCOM pointer (that is, if
 * it starts and ends with an at sign and does not contain any other at signs).
 * If so, returns the value inside the at signs (the ID). If not, returns the
 * empty string.
 * @private
 * @param {String} s the value to check for being a GEDCOM pointer
 * @return the pointed to ID, or empty string
 * @type String 
 */
GedcomLine.asPointer = function(s) {
	var r;
	r = /^@([^@]+)@$/.exec(s);
	if (r == null) {
		return "";
	}
	return r[1];
};

/**
 * Replaces double at signs with single at signs in the given string.
 * @private
 * @param {String} s string containing double at-signs
 * @return string s with double at-signs replaced with single at-signs
 * @type String
 */
GedcomLine.replaceAts = function(s) {
	if (!s || !s.replace) {
		return s;
	}
	return s.replace(/@@/g,"@");
};
