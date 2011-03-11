/**
 * @fileoverview
 * Defines the {@link Person} class.
 */

/**
 * @class Represents a person in a family tree.
 * @requires Partnership
 * @requires Rect
 * @requires Point
 * @requires Size
 * @requires Util
 * 
 * @constructor
 * @param {String} gid ID of this {@link Person}
 * @param {String} gname displayable name of this {@link Person}.
 * @param {Point} pos position of this {@link Person} in 2D space on a drop-line chart of the family tree.
 * @param {Array} revt array of {@link GedcomEvent}s for this {@link Person}.
 * @return new {@link Person}
 * @type Person
 */
function Person(gid,gname,pos,revt) {
	var that = this;
	Util.verifyType(this,"Person");

	/**
	 * ID of this person
	 * @private
	 * @type String
	 */
	this.gid = gid;

	/**
	 * Displayable name of this person
	 * @private
	 * @type String
	 */
	this.gname = gname;

	/**
	 * Array of events for this person
	 * @private
	 * @type Array
	 */
	this.revt = revt;

	/**
	 * Array of {@link Partnership}s that this {@link Person} is a child in. Could be more than one to allow for adoptive parents.
	 * @private
	 * @type Array
	 */
	this.childIn = [];

	/**
	 * Array of {@link Partnership}s that this {@link Person} is a spouse in.
	 * @private
	 * @type Array
	 */
	this.spouseIn = [];

	/**
	 * non-expanded display DIV of this {@link Person}
	 * @private
	 * @type HTMLElement
	 */
	this.div = this.createDiv(pos);

	/**
	 * expanded display DIV of this {@link Person}
	 * @private
	 * @type HTMLElement
	 */
	this.divExp = this.createDivExp(pos);

	/**
	 * @private
	 * @type Boolean
	 */
	this.viewExpanded = false;

	/**
	 * @private
	 * @type Boolean
	 */
	this.ignoreNextClick = false;
	
	this.div.onclick = function() {
		if (that.ignoreNextClick) {
			that.ignoreNextClick = false;
		} else {
			that.toggleView();
		}
	};

	this.divExp.onclick = this.div.onclick;

	/**
	 * @private
	 * @type Boolean
	 */
	this.savedZ = false;
	this.div.dragger = new Dragger(this.div,this,this.divExp);
	this.divExp.dragger = new Dragger(this.divExp,this,this.div);

	/**
	 * currently displayed DIV (<code>this.div</code> or <code>this.divExp</code>)
	 * @private
	 * @type HTMLElement
	 */
	this.divCur = this.div;
}

/**
 * Gets the ID of this {@link Person}.
 * @return ID
 * @type String
 */
Person.prototype.getID = function() {
	return this.gid;
};

/**
 * Gets the name of this {@link Person}.
 * @return name
 * @type String
 */
Person.prototype.toString = function() {
	return this.gname;
};

/**
 * Gets the Array of {@link Partnership}s that this {@link Person} is a child in.
 * @return child in {@link Partnership}s
 * @type Array
 */
Person.prototype.getChildIn = function() {
	return this.childIn;
};

/**
 * Gets the Array of {@link Partnership}s that this {@link Person} is a spouse in.
 * @return spouse in {@link Partnership}s
 * @type Array
 */
Person.prototype.getSpouseIn = function() {
	return this.spouseIn;
};

/**
 * Adds the given {@link Partnership} to this {@link Person}, where this person is
 * a spouse.
 * @param {Partnership} f spouse in {@link Partnership} to add
 */
Person.prototype.addSpouseIn = function(f) {
	this.spouseIn.push(f);
};

/**
 * Adds the given {@link Partnership} to this {@link Person}, where this person is
 * a child.
 * @param {Partnership} f child in {@link Partnership} to add
 */
Person.prototype.addChildIn = function(f) {
	this.childIn.push(f);
};

/**
 * Gets the Array of {@link GedcomEvent}s for this {@link Person}.
 * @return array in {@link GedcomEvent}s
 * @type Array
 */
Person.prototype.getEvents = function() {
	return this.revt;
};

/**
 * Creates a new DIV element for this {@link Person}'s non-expanded
 * display in the drop-line chart.
 * @private
 * @param {Point} pos position of upper-left corner
 * @return new div for non-expanded display
 * @type HTMLElement
 */
Person.prototype.createDiv = function(pos) {
	var div;
	div = Util.createHtmlElement("div");
	div.className = "person";
	div.position = "absolute";
	div.style.zIndex = 1;
	div.tabindex = 0;
	div.style.left = Util.px(pos.getX());
	div.style.top = Util.px(pos.getY());
	div.appendChild(Util.global.document.createTextNode(this.gname));
	Util.global.document.body.appendChild(div);
	return div;
};

/**
 * Creates a new DIV element for this {@link Person}'s expanded
 * display in the drop-line chart.
 * @private
 * @param {Point} pos position of upper-left corner
 * @return new div for expanded display
 * @type HTMLElement
 */
Person.prototype.createDivExp = function(pos) {
	var div, n, e;
	div = Util.createHtmlElement("div");
	div.className = "person expanded-person";
	div.style.position = "absolute";
	div.style.zIndex = 9;
	div.tabindex = 0;
	div.style.left = Util.px(pos.getX());
	div.style.top = Util.px(pos.getY());

	n = Util.global.document.createTextNode(this.gname);
	div.appendChild(n);
	
	e = this.createEventTable();
	div.appendChild(e);

	return div;
};

/**
 * Gets the current bounding rectangle of this {@link Person} on the drop-line chart.
 * @return current bounds
 * @type Rect
 */
Person.prototype.getRect = function() {
	return Rect.ofDiv(this.divCur);
};

/**
 * Calculates this {@link Person}'s related {@link Partnership}s.
 */
Person.prototype.calc = function() {
	Util.forEach(this.spouseIn, function(f) {
		f.calc();
	});
	Util.forEach(this.childIn, function(f) {
		f.calc();
	});
};

/**
 * Handles when this {@link Person}'s displayed DIV is moved.
 */
Person.prototype.onmoved = function() {
	this.ignoreNextClick = true;
	if (!this.savedZ) {
		this.savedZ = this.divCur.style.zIndex;
		this.divCur.style.zIndex = 10;
	}
	this.calc();
};

/**
 * Handles when this {@link Person}'s displayed DIV is finished being moved.
 */
Person.prototype.onmovedfinish = function() {
	if (this.savedZ) {
		this.divCur.style.zIndex = this.savedZ;
		this.savedZ = false;
	}
};

/**
 * Toggles the display of this {@link Person} between expanded and
 * non-expanded. 
 */
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

/**
 * Creates an HTML TABLE of this this {@link Person}'s events.
 * @private
 * @returns {HTMLElement}
 */
Person.prototype.createEventTable = function() {
	var table, thead, tfoot, tbody, th, tr, td;



	table = Util.createHtmlElement("table");
	table.className = "events";



	thead = Util.createHtmlElement("thead");
	tr = Util.createHtmlElement("tr");
	th = Util.createHtmlElement("th");
	th.appendChild(Util.global.document.createTextNode("event"));
	tr.appendChild(th);
	th = Util.createHtmlElement("th");
	th.appendChild(Util.global.document.createTextNode("date"));
	tr.appendChild(th);
	th = Util.createHtmlElement("th");
	th.appendChild(Util.global.document.createTextNode("place"));
	tr.appendChild(th);
	thead.appendChild(tr);
	table.appendChild(thead);



	tfoot = Util.createHtmlElement("tfoot");
	tfoot.style.height = "0px";
	table.appendChild(tfoot);



	tbody = Util.createHtmlElement("tbody");

	Util.forEach(this.revt, function(evt) {
		tr = Util.createHtmlElement("tr");

		td = Util.createHtmlElement("td");
		td.appendChild(Util.global.document.createTextNode(Util.safeStr(evt.getType())));
		tr.appendChild(td);
		td = Util.createHtmlElement("td");
		td.appendChild(Util.global.document.createTextNode(Util.safeStr(evt.getDate())));
		tr.appendChild(td);
		td = Util.createHtmlElement("td");
		td.appendChild(Util.global.document.createTextNode(Util.safeStr(evt.getPlace())));
		tr.appendChild(td);

		tbody.appendChild(tr);
	});

	table.appendChild(tbody);



	return table;
};