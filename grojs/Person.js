function Person(gid,gname,pos) {
	Util.verifyType(this,"Person");
	this.gid = gid;
	this.gname = gname;
	this.pos = pos;

	this.childIn = null;
	this.spouseIn = [];
}

Person.prototype.toString = function() {
	return this.name;
}

Person.prototype.addSpouseIn = function(f) {
	this.spouseIn.push(f);
}

Person.prototype.setChildIn = function(f) {
	this.childIn = f;
}
