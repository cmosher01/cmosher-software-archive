function Indi(n,x,y,tip) {
	this.name = n;
	this.tip = tip;
	this.deltax = 0;
	this.deltay = 0;
	this.curtip = null;

	this.childInFami = null;
	this.spouseInFami = new Array();

	this.div = document.createElement("div");
	this.div.style.zIndex = 1;
	this.div.setAttribute("tabindex","0");
//	this.div.onmouseover = tooltip;
//	this.div.onmouseout = tooltipoff;
//	this.div.onfocus = tooltip;
//	this.div.onblur = tooltipoff;
	this.div.style.left = x+"px";
	this.div.style.top = y+"px";

	this.div.dragger = new Dragger(this.div, this);

	var n = document.createTextNode(this.name);
	this.div.appendChild(n);

	document.body.appendChild(this.div);
}

Indi.prototype.left = function() {
	return this.div.offsetLeft;
};

Indi.prototype.top = function() {
	return this.div.offsetTop;
}

Indi.prototype.right = function() {
	return this.left()+this.div.offsetWidth;
}

Indi.prototype.bottom = function() {
	return this.top()+this.div.offsetHeight;
}

Indi.prototype.height = function() {
	return this.div.offsetHeight;
}

Indi.prototype.onmoved = function() {
	var i;
	for(i = 0; i < this.spouseInFami.length; i++) {
		this.spouseInFami[i].calc();
	}
	if (this.childInFami) {
		this.childInFami.calc();
	}
}

Indi.prototype.tooltip = function(e) {
	var tdiv = document.createElement("div");
	tdiv.style.left = e.currentTarget.style.left + "px";
	tdiv.style.top = e.currentTarget.style.top + "px";

	var name = document.createTextNode("Tool Tip Test");
	tdiv.appendChild(name);

	this.appendChild(tdiv);
	this.curtip = div;
}

Indi.prototype.tooltipoff = function(e) {
		this.curtip.parentNode.removeChild(this.curtip);
}
