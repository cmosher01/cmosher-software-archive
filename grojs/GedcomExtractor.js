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
	var root, extr;
	root = this.t.getRoot();
	extr = this;
	Util.forEach(root.getChildren(), function(node) {
		if (node.line.getTag() === "INDI") {
			extr.mperson[node.line.getID()] = extr.extractPerson(node);
		}
	});
	Util.forEach(root.getChildren(), function(node) {
		if (node.line.getTag() === "FAM") {
			extr.mpartnership[node.line.getID()] = extr.extractParnership(node);
		}
	});
};

GedcomExtractor.prototype.extractPerson = function(indi) {
	var nam, xy, m;

	nam = "[unknown]";
	xy = new Point(0,0);
	Util.forEach(indi.getChildren(), function(node) {
		if (node.line.getTag() === "NAME") {
			nam = node.line.getVal().replace(/\//g,"");
		} else if (node.line.getTag() === "_XY") {
			m = /(\d+)\s+(\d+)/.exec(node.line.getVal());
			if (m !== null) {
				xy = new Point(m[1],m[2]);
			}
		}
	});

	return new Person(indi.line.getID(),nam,xy);
};


GedcomExtractor.prototype.extractParnership = function(fam) {
	var husb, wife, rchil, extr;
	extr = this;
	husb = null;
	wife = null;
	rchil = [];
	Util.forEach(fam.getChildren(), function(node) {
		if (node.line.getTag() === "HUSB") {
			husb = extr.mperson[node.line.getPointer()];
		} else if (node.line.getTag() === "WIFE") {
			wife = extr.mperson[node.line.getPointer()];
		} else if (node.line.getTag() === "CHIL") {
			rchil.push(extr.mperson[node.line.getPointer()]);
		}
	});
	return new Partnership(fam.line.getID(),husb,wife,rchil);
};
