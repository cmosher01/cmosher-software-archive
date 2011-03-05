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



Person.prototype.left = function() {
	return this.div.offsetLeft;
};

Person.prototype.top = function() {
	return this.div.offsetTop;
};

Person.prototype.width = function() {
	return this.div.offsetWidth;
};

Person.prototype.height = function() {
	return this.div.offsetHeight;
};

Person.prototype.right = function() {
	return this.left()+this.width();
};

Person.prototype.bottom = function() {
	return this.top()+this.height();
};

Person.prototype.midx = function() {
	return (this.left()+this.right())/2;
};

Person.prototype.midy = function() {
	return (this.top()+this.bottom())/2;
};


Person.prototype.onmoved = function() {
	Util.forEach(this.spouseIn, function(f) {
		f.calc();
	});
	Util.forEach(this.childIn, function(f) {
		f.calc();
	});
}
