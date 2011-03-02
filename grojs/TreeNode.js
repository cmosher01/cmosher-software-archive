function TreeNode() {
	if (!(this instanceof TreeNode)) {
		throw Error("error creating object (missing new operator?)");
	}
	this.parent = null;
	this.children = [];
}



TreeNode.prototype.getChildren = function() {
	return this.children;
}

TreeNode.prototype.getParent = function() {
	return this.parent;
}



TreeNode.prototype.removeChild = function(child) {
	var c;

	if (child === null || child === undefined || child.parent !== this) {
		throw new Error("given TreeNode is not a child of this TreeNode");
	}

	for (c in this.children) {
		if (this.children.hasOwnProperty(c)) {
			if (this.children[c] === child) {
				delete this.children[c];
				child.parent = null;
				this.children = Util.prototype.consolodate(this.children);
				return;
			}
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
