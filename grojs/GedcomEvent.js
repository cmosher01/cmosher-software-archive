function GedcomEvent(typ,gdate,place) {
	this.typ = typ;
	this.gdate = gdate;
	this.place = place;
}

GedcomEvent.prototype.getType = function() {
	return this.typ;
};

GedcomEvent.prototype.getDate = function() {
	return this.gdate;
};

GedcomEvent.prototype.getPlace = function() {
	return this.place;
};
