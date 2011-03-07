function Person(gid,gname,pos,revt) {
	var that = this;
	Util.verifyType(this,"Person");

	this.gid = gid;
	this.gname = gname;
	this.revt = revt;

	this.childIn = []; // allow for adoptive parents
	this.spouseIn = [];

	this.div = Person.createDiv(gname,pos);
	this.createDivExp(pos);

	this.viewExpanded = false;
	this.ignoreNextClick = false;
	this.div.onclick = function() {
		if (that.ignoreNextClick) {
			that.ignoreNextClick = false;
		} else {
			that.toggleView();
		}
	};

	this.divExp.onclick = this.div.onclick;

	this.savedZ = false;
	this.div.dragger = new Dragger(this.div,this,this.divExp);
	this.divExp.dragger = new Dragger(this.divExp,this,this.div);

	this.divCur = this.div;
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

Person.prototype.getEvents = function() {
	return this.revt;
};







Person.createDiv = function(s,pos) {
	var div;
	div = document.createElement("div");
	div.className = "person";
	div.style.position = "absolute";
	div.style.zIndex = 1;
	div.setAttribute("tabindex","0");
	div.style.left = Util.px(pos.getX());
	div.style.top = Util.px(pos.getY());
	div.appendChild(document.createTextNode(s));
	document.body.appendChild(div);
	return div;
};

Person.prototype.createDivExp = function(pos) {
	var n, e;
	this.divExp = document.createElement("div");
	this.divExp.className = "person expanded-person";
	this.divExp.style.position = "absolute";
	this.divExp.style.zIndex = 9;
	this.divExp.setAttribute("tabindex","0");
	this.divExp.style.left = Util.px(pos.getX());
	this.divExp.style.top = Util.px(pos.getY());

	n = document.createTextNode(this.gname);
	this.divExp.appendChild(n);
	
	e = this.createEventTable();
	this.divExp.appendChild(e);
};

Person.prototype.getRect = function() {
	return Rect.ofDiv(this.divCur);
};

Person.prototype.calc = function() {
	Util.forEach(this.spouseIn, function(f) {
		f.calc();
	});
	Util.forEach(this.childIn, function(f) {
		f.calc();
	});
};

Person.prototype.onmoved = function() {
	this.ignoreNextClick = true;
	if (!this.savedZ) {
		this.savedZ = this.divCur.style.zIndex;
		this.divCur.style.zIndex = 10;
	}
	this.calc();
};

Person.prototype.onmovedfinish = function() {
	if (this.savedZ) {
		this.divCur.style.zIndex = this.savedZ;
		this.savedZ = false;
	}
};

Person.prototype.toggleView = function() {
	if (this.viewExpanded) {
		this.viewExpanded = false;
		this.divExp.parentNode.replaceChild(this.div,this.divExp);
		this.divCur = this.div;
	} else {
		this.viewExpanded = true;
		this.div.parentNode.replaceChild(this.divExp,this.div);
		this.divCur = this.divExp;
	}
	this.calc();
};

Person.prototype.createEventTable = function() {
	var table, thead, tfoot, tbody, th, tr, td;



	table = document.createElement("table");
	table.className = "events";



	thead = document.createElement("thead");
	tr = document.createElement("tr");
	th = document.createElement("th");
	th.appendChild(document.createTextNode("event"));
	tr.appendChild(th);
	th = document.createElement("th");
	th.appendChild(document.createTextNode("date"));
	tr.appendChild(th);
	th = document.createElement("th");
	th.appendChild(document.createTextNode("place"));
	tr.appendChild(th);
	thead.appendChild(tr);
	table.appendChild(thead);



	tfoot = document.createElement("tfoot");
	tfoot.style.height = "0px";
	table.appendChild(tfoot);



	tbody = document.createElement("tbody");

	Util.forEach(this.revt, function(evt) {
		tr = document.createElement("tr");

		td = document.createElement("td");
		td.appendChild(document.createTextNode(Util.safeStr(evt.getType())));
		tr.appendChild(td);
		td = document.createElement("td");
		td.appendChild(document.createTextNode(Util.safeStr(evt.getDate())));
		tr.appendChild(td);
		td = document.createElement("td");
		td.appendChild(document.createTextNode(Util.safeStr(evt.getPlace())));
		tr.appendChild(td);

		tbody.appendChild(tr);
	});

	table.appendChild(tbody);



	return table;
};
