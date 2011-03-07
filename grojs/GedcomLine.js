/* PUBLIC */

/* constructor */

function GedcomLine(level, gid, tag, val) {
	Util.verifyType(this,"GedcomLine");

	if (!arguments.length) {
		return this;
	}

	this.level = parseInt(level);
	this.gid = GedcomLine.asPointer(gid);
	this.tag = Util.safeStr(tag).toUpperCase();
	this.pointer = GedcomLine.asPointer(val);
	if (this.pointer) {
		this.val = Util.safeStr();
	} else {
		this.val = GedcomLine.replaceAts(Util.safeStr(val));
	}
}

/* simple accessor methods */

GedcomLine.prototype.getLevel = function() {
	return this.level;
};

GedcomLine.prototype.getID = function() {
	return this.gid;
};

GedcomLine.prototype.getTag = function() {
	return this.tag;
};

GedcomLine.prototype.getVal = function() {
	return this.val;
};

GedcomLine.prototype.getPointer = function() {
	return this.pointer;
};

/* complex accessor methods */

GedcomLine.prototype.hasID = function() {
	return !!this.getID();
};

GedcomLine.prototype.isPointer = function() {
	return !!this.getPointer();
};

/* mutator methods */

GedcomLine.prototype.concat = function(c) {
	switch (c.getTag()) {
		case "CONC": this.val += c.getVal(); break;
		case "CONT": this.val += "\n"+c.getVal(); break;
	}
};

/* static factory methods */

GedcomLine.parse = function(s) {
	var r = /^(\d+)\s+(?:(@[^@]+@)\s+)?(\S+)(?:\s(.*))?$/.exec(s);
	if (r == null) {
		throw new Error("Gedcom line has invalid syntax: "+s);
	}
	return new GedcomLine(r[1],r[2],r[3],r[4]);
};


/* PRIVATE */

/* static methods */

GedcomLine.asPointer = function(s) {
	var r;
	r = /^@([^@]+)@$/.exec(s);
	if (r == null) {
		return "";
	}
	return r[1];
};

GedcomLine.replaceAts = function(s) {
	if (!s || !s.replace) {
		return s;
	}
	return s.replace(/@@/g,"@");
};
