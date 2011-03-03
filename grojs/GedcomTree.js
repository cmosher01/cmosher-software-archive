function GedcomTree() {
	Util.verifyType(this,"GedcomTree");
	this.root = new TreeNode();
	this.prevNode = this.root;
	this.prevLevel = -1;
	this.mapIDtoNode = {};
}

/**
 * Gets the root, which is a TreeNode. Each TreeNode will
 * have a property called "line" that is a GedcomLine.
 */
GedcomTree.prototype.getRoot = function() {
	return this.root;
}

/**
 * Looks up the node with the given ID.
 */
GedcomTree.prototype.getNode = function(gid) {
	return this.mapIDtoNode[gid];
}

/**
 * Adds the given line to this tree. Note that this method must
 * be called in the same sequence as lines in the gedcom file.
 */
GedcomTree.prototype.appendLine = function(line) {
	var c, i, v, p;

	Util.verifyType(line,"GedcomLine");

	v = line.getLevel();
	c = this.prevLevel + 1 - v;
	if (c < 0) {
		throw new Error("Invalid level: " + v);
	}
	this.prevLevel = v;

	p = this.prevNode;
	for (i = 0; i < c; i++) {
		p = p.getParent();
	}

	this.prevNode = new TreeNode();
	this.prevNode.line = line; // create "line" property in tree node
	p.addChild(this.prevNode);

	if (line.hasID()) {
		this.mapIDtoNode[line.getID()] = this.prevNode;
	}
}

GedcomTree.prototype.concatenate = function() {
	this.concatenatePrivateHelper(this.getRoot());
}

GedcomTree.prototype.concatenatePrivateHelper = function(p) {
	var rdel, tre;
	tre = this;
	rdel = [];

	Util.forEach(p.getChildren(), function(c) {
		tre.concatenatePrivateHelper(c);
		switch (c.line.getTag()) {
			case "CONT":
			case "CONC":
				p.line.concat(c.line);
				rdel.push(c);
		}
	});
	Util.forEach(rdel, function(c) {
		c.removeFromParent();
	});
}

/**
 * Parses the given gedcom string and adds the records
 * to this tree. Can be called multiple times for chunks of
 * the input gedcom file, but must be called in order.
 */
GedcomTree.prototype.parseAppend = function(gc) {
	var rs, tre;

	rs = Util.getLines(gc);

	tre = this;
	Util.forEach(rs, function(s) {
		if (s.length > 0) { // skip blank lines
			// parse the line and add it to this tree
			tre.appendLine(GedcomLine.parse(s));
		}
	});
}

/**
 * Static factory method. Creates a GedcomTree by parsing the
 * given gedcom string. The gedcom must be complete.
 * Can be called as a member method, or as a static method.
 */
GedcomTree.parse = function(gc) {
	var g = new GedcomTree();
	g.parseAppend(gc);
	g.concatenate();
	return g;
}
