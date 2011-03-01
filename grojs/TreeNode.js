function TreeNode() {
	if (!(this instanceof TreeNode)) {
		throw Error("error creating object (missing new operator?)");
	}
	this.parent = null;
	this.children = [];
}



TreeNode.prototype.getChildren = function() {
	return this.children.concat();
}

TreeNode.prototype.getParent = function() {
	return this.parent;
}



TreeNode.prototype.removeChild = function(child) {
	var c;
	if (!(child instanceof TreeNode)) {
		throw new Error("child must be a TreeNode");
	}
	if (child.parent !== this) {
		throw new Error("given TreeNode is not a child of this TreeNode");
	}

	for (c in this.children) {
		if (this.children[c] === child) {
			delete this.children[c];
			child.parent = null;
			return;
		}
	}
}

TreeNode.prototype.removeFromParent = function() {
	if (this.parent === null) {
		return;
	}
	this.parent.removeChild(this);
}

TreeNode.prototype.addChild = function(child) {
	if (!(child instanceof TreeNode)) {
		throw new Error("child must be a TreeNode");
	}
	child.removeFromParent();
	this.children.push(child);
	child.parent = this;
}
