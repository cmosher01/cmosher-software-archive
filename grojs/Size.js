function Size(w,h) {
	Util.verifyType(this,"Size");
	this.w = parseInt(w);
	this.h = parseInt(h);
}

Size.prototype.getWidth = function() {
	return this.w;
};

Size.prototype.getHeight = function() {
	return this.h;
};

Size.prototype.toString = function() {
	return "WxH="+this.getWidth()+"x"+this.getHeight();
};
