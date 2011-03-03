function Person(gid,gname,pos) {
	Util.prototype.verifyType(this,"Person");
	this.gid = gid;
	this.gname = gname;
	this.pos = pos;
}

Person.prototype.toString = function() {
	return this.name;
}
