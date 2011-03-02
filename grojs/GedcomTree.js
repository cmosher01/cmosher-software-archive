function GedcomTree() {
	if (!(this instanceof GedcomTree)) {
		throw new Error("error creating object (missing new operator?)");
	}

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

	if (!(line instanceof GedcomLine)) {
		throw new Error("line is not a GedcomLine");
	}

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

	Util.prototype.forEach(p.getChildren(), function(c) {
		tre.concatenatePrivateHelper(c);
		switch (c.line.getTag()) {
			case "CONT":
				p.line = p.line.cont(c.line.getVal());
				rdel.push(c);
			break;
			case "CONC":
				p.line = p.line.conc(c.line.getVal());
				rdel.push(c);
			break;
		}
	});
	Util.prototype.forEach(rdel, function(c) {
		c.removeFromParent();
	});
}

/**
 * Parses the given lines (array of strings) and adds them
 * to this tree. Can be called multiple times for chunks of
 * the input gedcom file, but must be called in order.
 */
GedcomTree.prototype.parse = function(rs) {
	var tre = this;
	Util.prototype.forEach(rs, function(s) {
		if (s.length > 0) {
			tre.appendLine(GedcomLine.prototype.parse(s));
		}
	});
}
