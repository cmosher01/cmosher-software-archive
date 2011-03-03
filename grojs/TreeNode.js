function TreeNode() {
	Util.prototype.verifyType(this,"TreeNode");
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

	Util.prototype.verifyType(child,"TreeNode");

	this.children = Util.prototype.remove(child,this.children);
	child.parent = null;
}

TreeNode.prototype.removeFromParent = function() {
	if (this.parent === null) {
		return;
	}
	this.parent.removeChild(this);
}

TreeNode.prototype.addChild = function(child) {
	Util.prototype.verifyType(child,"TreeNode");

	child.removeFromParent();
	this.children.push(child);
	child.parent = this;
}
