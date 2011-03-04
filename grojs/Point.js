function Point(x,y) {
	Util.verifyType(this,"Point");
	this.x = parseInt(x);
	this.y = parseInt(y);
}

Point.prototype.getX = function() {
	return this.x;
};

Point.prototype.getY = function() {
	return this.y;
};

Point.prototype.toString = function() {
	return "("+this.getX()+","+this.getY()+")";
};
