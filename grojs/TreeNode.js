/**
 * @fileoverview
 * Defines the {@link TreeNode} class.
 */

/**
 * @class Represents a node and the tree rooted at it.
 * @requires Util
 * @constructor
 * @return new {@link TreeNode}
 * @type TreeNode
 */
function TreeNode() {
	Util.verifyType(this,"TreeNode");

	/**
	 * parent node, or null if this is the root
	 * @private
	 * @type TreeNode
	 */
	this.parent = null;

	/**
	 * children nodes
	 * @private
	 * @type Array
	 */
	this.children = [];
}



/**
 * Gets the direct child {@link TreeNode}s of this {@link TreeNode}.
 * @return child {@link TreeNode}s
 * @type Array
 */
TreeNode.prototype.getChildren = function() {
	return this.children;
};

/**
 * Gets the direct parent {@link TreeNode} of this {@link TreeNode}.
 * Returns <code>null</code> if this is the root node.
 * @returns parent {@link TreeNode}
 * @type TreeNode
 */
TreeNode.prototype.getParent = function() {
	return this.parent;
};



/**
 * Remove the given child {@link TreeNode} from this {@link TreeNode}.
 * If the given node is not a child of this node, then this method does
 * not alter this node.
 * Note that the given child will be a root node (with no parent) after
 * this method returns (whether or not the removal takes place).
 * @param {TreeNode} child the child to remove
 * @throws if the given child is not a {@link TreeNode}
 */
TreeNode.prototype.removeChild = function(child) {
	Util.verifyType(child,"TreeNode");

	this.children = Util.remove(child,this.children);
	child.setParent(null);
};

/**
 * Removes this {@link TreeNode} from its parent, if it has one.
 */
TreeNode.prototype.removeFromParent = function() {
	if (this.parent === null) {
		return;
	}
	this.parent.removeChild(this);
};

/**
 * Appends the given {@link TreeNode} as a child of this {@link TreeNode}.
 * @param {TreeNode} child the child to append
 * @throws if the given child is not a {@link TreeNode}
 */
TreeNode.prototype.addChild = function(child) {
	Util.verifyType(child,"TreeNode");

	child.removeFromParent();
	this.children.push(child);
	child.setParent(this);
};





/**
 * Sets the direct parent {@link TreeNode} of this {@link TreeNode}.
 * @private
 * @param {TreeNode} parent
 */
TreeNode.prototype.setParent = function(parent) {
	this.parent = parent;
};
