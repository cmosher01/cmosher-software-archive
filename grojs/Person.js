function Person(gid,gname,pos) {
	Util.verifyType(this,"Person");

	this.gid = gid;
	this.gname = gname;

	this.childIn = null;
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

Person.prototype.setChildIn = function(f) {
	this.childIn = f;
};







Person.createDiv = function(s,pos) {
	var div;
	div = document.createElement("div");
	div.className = "person";
	div.style.position = "absolute";
	div.style.opacity = .8;
	div.style.zIndex = 1;
	div.setAttribute("tabindex","0");
	div.style.left = pos.getX()+"px";
	div.style.top = pos.getY()+"px";
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



Person.prototype.onmoved = function() {
	Util.forEach(this.spouseIn, function(f) {
		f.calc();
	});
	if (this.childIn) {
		this.childIn.calc();
	}
}
