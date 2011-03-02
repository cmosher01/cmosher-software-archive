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
	this.tag = Util.prototype.safeStr(tag).toUpperCase();
	this.pointer = this.asPointer(val);
	if (this.pointer) {
		this.val = Util.prototype.safeStr();
	} else {
		this.val = this.replaceAts(Util.prototype.safeStr(val));
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

/* mutator methods */

GedcomLine.prototype.concat = function(c) {
	switch (c.getTag()) {
		case "CONC": this.val += c.getVal(); break;
		case "CONT": this.val += "\n"+c.getVal(); break;
	}
}

/* factory methods */

GedcomLine.prototype.parse = function(s) {
	if (!(this instanceof GedcomLine)) {
		return new GedcomLine().parse(s);
	}
	var r = /^(\d+)\s+(?:(@[^@]+@)\s+)?(\S+)(?:\s(.*))?$/.exec(s);
	if (r === null) {
		throw new Error("Gedcom line has invalid syntax: "+s);
	}
	this.constructor(r[1],r[2],r[3],r[4]);
	return this;
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
