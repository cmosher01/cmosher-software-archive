function Person(gid,gname,pos) {
	Util.verifyType(this,"Person");

	this.gid = gid;
	this.gname = gname;

	this.childIn = []; // allow for adoptive parents
	this.spouseIn = [];

	this.div = Person.createDiv(gname,pos);

	this.div.dragger = new Dragger(this.div, this);
}

Person.prototype.getID = function() {
	return this.gid;
};

Person.prototype.toString = function() {
	return this.gname;
};

Person.prototype.getChildIn = function() {
	return this.childIn;
};

Person.prototype.getSpouseIn = function() {
	return this.spouseIn;
};

Person.prototype.addSpouseIn = function(f) {
	this.spouseIn.push(f);
};

Person.prototype.addChildIn = function(f) {
	this.childIn.push(f);
};







Person.createDiv = function(s,pos) {
	var div;
	div = document.createElement("div");
	div.className = "person";
	div.style.position = "absolute";
	div.style.opacity = .8;
	div.style.zIndex = 1;
	div.setAttribute("tabindex","0");
	div.style.left = Util.px(pos.getX());
	div.style.top = Util.px(pos.getY());
	div.appendChild(document.createTextNode(s));
	document.body.appendChild(div);
	return div;
};

Person.prototype.getRect = function() {
	return Rect.ofDiv(this.div);
};

Person.prototype.onmoved = function() {
	this.div.style.zIndex = 2;
	Util.forEach(this.spouseIn, function(f) {
		f.calc();
	});
	Util.forEach(this.childIn, function(f) {
		f.calc();
	});
};

Person.prototype.onmovedfinish = function() {
	this.div.style.zIndex = 1;
};
