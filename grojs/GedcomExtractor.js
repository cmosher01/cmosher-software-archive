function GedcomExtractor(gedcomtree) {
	Util.prototype.verifyType(this,"GedcomExtractor");
	this.t = gedcomtree;
	Util.prototype.verifyType(this.t,"GedcomTree");
	this.mperson = {};
	this.mpartnership = {};
}

GedcomExtractor.prototype.extract = function() {
	var root;
	root = this.t.getRoot();
	Util.prototype.forEach(root.getChildren(), function (node) {
		if (node.line.getTag() === "INDI") {
			this.mperson[node.line.getID()] = this.extractPerson(node);
		}
	});
	Util.prototype.forEach(root.getChildren(), function (node) {
		if (node.line.getTag() === "FAM") {
			this.mpartnership[node.line.getID()] = this.extractParnership(node);
		}
	});
}

GedcomExtractor.prototype.extractPerson = function(indi) {
	var p, nam, xy, m;

	nam = "[unknown]";
	xy = new Point(0,0);
	Util.prototype.forEach(indi.getChildren(), function (node) {
		if (node.line.getTag() === "NAME") {
			nam = node.line.getVal().replace(/\//g,"");
		}
		if (node.line.getTag() === "_XY") {
			m = /(\d+)\s+(\d+)/.exec(node.line.getVal());
			xy = new Point(m[1],m[2]);
		}
	}

	p = new Person(indi.line.getID(),nam,xy);
	return p;
}


GedcomExtractor.prototype.extractParnership = function(node) {
	var p;
	p = new Parnership();
	return p;
}
