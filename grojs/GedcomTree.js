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
