function GedcomExtractor(gedcomtree) {
	Util.verifyType(this,"GedcomExtractor");
	this.t = gedcomtree;
	Util.verifyType(this.t,"GedcomTree");
	this.mperson = {};
	this.mpartnership = {};

	this.extract();
}

GedcomExtractor.prototype.calc = function() {
	Util.forEach(this.mpartnership, function(p) {
		p.calc();
	});
};

/* private mutator (init) methods */

GedcomExtractor.prototype.extract = function() {
	var rchil, that;
	rchil = this.t.getRoot().getChildren();
	that = this;
	Util.forEach(rchil, function(node) {
		if (node.line.getTag() === "INDI") {
			that.mperson[node.line.getID()] = that.extractPerson(node);
		}
	});
	Util.forEach(rchil, function(node) {
		if (node.line.getTag() === "FAM") {
			that.mpartnership[node.line.getID()] = that.extractParnership(node);
		}
	});
};

GedcomExtractor.prototype.extractPerson = function(indi) {
	var that, nam, xy, m, revt;

	that = this;

	nam = "[unknown]";
	revt = [];
	xy = new Point(0,0);
	Util.forEach(indi.getChildren(), function(node) {
		if (node.line.getTag() === "NAME") {
			nam = node.line.getVal().replace(/\//g,"");
		} else if (node.line.getTag() === "_XY") {
			m = /(\d+)\s+(\d+)/.exec(node.line.getVal());
			if (m !== null) {
				xy = new Point(m[1],m[2]);
			}
		} else if (GedcomTag.isIndiEvent(node.line.getTag())) {
			revt.push(that.extractEvent(node));
		}
	});

	return new Person(indi.line.getID(),nam,xy,revt);
};


GedcomExtractor.prototype.extractParnership = function(fam) {
	var that, husb, wife, rchil, revt;
	that = this;
	husb = null;
	wife = null;
	rchil = [];
	revt = [];
	Util.forEach(fam.getChildren(), function(node) {
		if (node.line.getTag() === "HUSB") {
			husb = that.mperson[node.line.getPointer()];
		} else if (node.line.getTag() === "WIFE") {
			wife = that.mperson[node.line.getPointer()];
		} else if (node.line.getTag() === "CHIL") {
			rchil.push(that.mperson[node.line.getPointer()]);
		} else if (GedcomTag.isFamEvent(node.line.getTag())) {
			revt.push(that.extractEvent(node));
		}
	});
	return new Partnership(fam.line.getID(),husb,wife,rchil,revt);
};

GedcomExtractor.prototype.extractEvent = function(evt) {
	var typ, gdate, place;
	typ = this.extractEventName(evt);
	Util.forEach(evt.getChildren(), function(node) {
		if (node.line.getTag() === "DATE") {
			gdate = node.line.getVal(); // TODO parse date
		} else if (node.line.getTag() === "PLAC") {
			place = node.line.getVal();
		}
	});
	return new GedcomEvent(typ,gdate,place);
};

GedcomExtractor.prototype.extractEventName = function(evt) {
	var nam, val;
	nam = "";
	val = "";

	if (evt.line.getTag() === "EVEN") {
		Util.forEach(evt.getChildren(), function(node) {
			if (node.line.getTag() === "TYPE") {
				nam = node.line.getVal();
			}
		});
	}
	if (!nam) {
		nam = GedcomTag.getEventName(evt.line.getTag());
		val = evt.line.getVal();
		if (val) {
			nam += ": "+val;
		}
	}
	return nam;
};
