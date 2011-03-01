/* constructor */

function GedcomLine(level, gid, tag, val) {
	if (this === window) {
		throw Error("new operator is required");
	}
	if (!arguments.length) {
		return this;
	}

	this.level = level;
	this.gid = this.asPointer(gid);
	this.tag = tag;
	this.pointer = this.asPointer(val);
	this.val = "";
	if (!this.pointer) {
		this.val = this.replaceAts(val);
	}
}

/* simple accessor methods */

GedcomLine.prototype.getLevel = function() {
	return this.level;
}

GedcomLine.prototype.getID = function() {
	return this.gid;
}

GedcomLine.prototype.getTag = function() {
	return this.tag;
}

GedcomLine.prototype.getVal = function() {
	return this.val;
}

GedcomLine.prototype.getPointer = function() {
	return this.pointer;
}

/* complex accessor methods */

GedcomLine.prototype.hasID = function() {
	return !!this.getID();
}

GedcomLine.prototype.isPointer = function() {
	return !!this.getPointer();
}

/* factory methods */

GedcomLine.prototype.contVal = function(contLine) {
	return this.concVal("\n"+contLine);
}

GedcomLine.prototype.concVal = function(concLine) {
	var that;
	that = new GedcomLine();
	that.level = this.getLevel();
	that.gid = this.getID();
	that.tag = this.getTag();
	that.pointer = this.getPointer();
	that.val = this.getVal()+concLine;
	return that;
}

/* private static methods */

GedcomLine.prototype.asPointer = function(s) {
	var r;
	r = /^@([^@]+)@$/.exec(s);
	if (r === null) {
		return "";
	}
	return r[1];
}

GedcomLine.prototype.replaceAts = function(s) {
	if (!s || !s.replace) {
		return s;
	}
	return s.replace(/@@/g,"@");
}
