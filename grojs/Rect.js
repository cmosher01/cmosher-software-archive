function Rect(pos,siz) {
	Util.verifyType(this,"Rect");
	this.pos = pos;
	Util.verifyType(this.pos,"Point");
	this.siz = siz;
	Util.verifyType(this.siz,"Size");
}

Rect.prototype.getPos = function() {
	return this.pos;
};

Rect.prototype.getSize = function() {
	return this.siz;
};

Rect.prototype.getLeft = function() {
	return this.pos.getX();
};

Rect.prototype.getWidth = function() {
	return this.siz.getWidth();
};

Rect.prototype.getTop = function() {
	return this.pos.getY();
};

Rect.prototype.getHeight = function() {
	return this.siz.getHeight();
};

Rect.prototype.getRight = function() {
	return this.getLeft()+this.getWidth();
};

Rect.prototype.getBottom = function() {
	return this.getTop()+this.getHeight();
};

Rect.prototype.getMidX = function() {
	return Math.round((this.getLeft()+this.getRight())/2);
};

Rect.prototype.getMidY = function() {
	return Math.round((this.getTop()+this.getBottom())/2);
};

Rect.ofDiv = function(div) {
	return new Rect(new Point(div.offsetLeft,div.offsetTop),new Size(div.offsetWidth,div.offsetHeight));
};
