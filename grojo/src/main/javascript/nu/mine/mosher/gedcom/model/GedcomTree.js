/**
 * @fileoverview
 * Defines the {@link GedcomTree} class.
 */

/**
 * @class Represents a GEDCOM file, as a hierarchical tree.
 * @requires GedcomLine
 * @requires TreeNode
 * @requires Util
 * 
 * @constructor
 * @return new {@link GedcomTree}
 * @type GedcomTreee
 */
function GedcomTree() {
	Util.verifyType(this,"GedcomTree");

	/**
	 * The root of the entire tree. We add a "line" property to each {@link TreeNode} for its {@link GedcomLine}. 
	 * @private
	 * @type TreeNode
	 */
	this.root = new TreeNode();

	/**
	 * previously added node
	 * @private
	 * @type TreeNode
	 */
	this.prevNode = this.root;

	/**
	 * level number of previously added node
	 * @private
	 * @type Number
	 */
	this.prevLevel = -1;

	/**
	 * Map of GEDCOM line IDs to nodes (lines).
	 * @private
	 * @type Object
	 */
	this.mapIDtoNode = {};
}

/**
 * Gets the root, which is a {@link TreeNode}. Each {@link TreeNode} will
 * have a property called "line" that is a {@link GedcomLine}. The children of
 * the returned {@link TreeNode} will be the top-level (level number zero)
 * records in the GEDCOM file.
 * @return root {@link TreeNode} with {@link GedcomLine} <code>line</code> property
 * @type TreeNode
 */
GedcomTree.prototype.getRoot = function() {
	return this.root;
};

/**
 * Looks up the node with the given ID. If not found, returns <code>undefined</code>.
 * @param {String} gid ID to look up
 * @return {@link TreeNode} with {@link GedcomLine} <code>line</code> property, or <code>undefined</code>.
 * @type TreeNode
 */
GedcomTree.prototype.getNode = function(gid) {
	return this.mapIDtoNode[gid];
};

/**
 * Adds the given line to this {@link GedcomTree}. Note that this method must
 * be called in the same sequence as lines in the GEDCOM file.
 * @param {GedcomLine} line the line to append
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
};

/**
 * Concatenates any CONC or CONT lines in this {@link GedcomTree}
 * to their parent lines, and removes the CONC and CONT lines.
 */
GedcomTree.prototype.concatenate = function() {
	this.concatenatePrivateHelper(this.getRoot());
};

/**
 * Helper method for concatenate.
 * @private
 * @param {TreeNode} p root of tree to process
 */
GedcomTree.prototype.concatenatePrivateHelper = function(p) {
	var rdel, tre, pline;
	tre = this;
	rdel = [];

	Util.forEach(p.getChildren(), function(c) {
		tre.concatenatePrivateHelper(c);
		pline = p.line; // line is GedcomLine, added by appendLine
		switch (c.line.getTag()) {
			case "CONT":
			case "CONC":
				pline.concat(c.line);
				rdel.push(c);
		}
	});
	Util.forEach(rdel, function(c) {
		c.removeFromParent();
	});
};

/**
 * Parses the given GEDCOM string and adds the records
 * to this {@link GedcomTree}. Can be called multiple times for chunks of
 * the input GEDCOM file, but must be called in order.
 * @param {String} gc entire GEDCOM file
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
};

/**
 * Creates a {@link GedcomTree} by parsing the
 * given GEDCOM string. The GEDCOM must be complete.
 * @param {String} gc entire GEDCOM file
 * @return new {@link GedcomTree} representing the given file gc
 * @type GedcomTree
 */
GedcomTree.parse = function(gc) {
	var g = new GedcomTree();
	g.parseAppend(gc);
	g.concatenate();
	return g;
};
