/* PUBLIC */

/* constructor */

function GedcomLine(level, gid, tag, val) {
	if (!(this instanceof GedcomLine)) {
		throw new Error("error creating object (missing new operator?)");
	}
	if (!arguments.length) {
		return this;
	}

	this.level = parseInt(level);
	this.gid = this.asPointer(gid);
	this.tag = tag === undefined ? "" : tag;
	this.pointer = this.asPointer(val);
	if (this.pointer) {
		this.val = "";
	} else {
		this.val = this.replaceAts(val === undefined ? "" : val);
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

GedcomLine.prototype.conc = function(concLine) {
	var that;
	that = new GedcomLine();
	that.level = this.getLevel();
	that.gid = this.getID();
	that.tag = this.getTag();
	that.pointer = this.getPointer();
	that.val = this.getVal()+concLine;
	return that;
}

GedcomLine.prototype.cont = function(contLine) {
	return this.conc("\n"+contLine);
}

GedcomLine.prototype.parse = function(s) {
	var r = /^(\d+)\s+(?:(@[^@]+@)\s+)?(\S+)(?:\s(.*))?$/.exec(s);
	if (r === null) {
		throw new Error("Gedcom line has invalid syntax: "+s);
	}
	return new GedcomLine(r[1],r[2],r[3],r[4]);
}


/* PRIVATE */

/* static methods */

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
