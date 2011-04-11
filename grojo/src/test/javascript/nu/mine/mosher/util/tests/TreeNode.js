(function($,doh) {
	"use strict";

	var SUITE = "nu.mine.mosher.util.tests.TreeNode";

	$.provide(SUITE);

	$.require("nu.mine.mosher.util.TreeNode");
	var TreeNode = nu.mine.mosher.util.TreeNode;
	$.require("nu.mine.mosher.util.Util");
	var Util = nu.mine.mosher.util.Util;

	doh.register(SUITE,[

	function nominalChild() {
		var a, b, r, i, c;

		a = new TreeNode();
		a.obj = "a";
		b = new TreeNode();
		b.obj = "b";

		a.addChild(b);
		r = a.getChildren();

		i = 0;
		Util.forEach(r,function() {i++;});
		doh.is(i, 1);

		Util.forEach(r,function(v) {
			doh.is(v.obj, "b");
		});

		doh.is(b.getParent().obj, "a");
	},

	function nominalRemoveChild() {
		var a, b, r, i, c;

		a = new TreeNode();
		a.obj = "a";
		b = new TreeNode();
		b.obj = "b";

		a.addChild(b);
		a.removeChild(b);
		r = a.getChildren();

		i = 0;
		Util.forEach(r,function() {i++;});
		doh.is(i, 0);

		doh.is(b.getParent(), null);
	},

	function nominalRemoveFromParent() {
		var a, b, r, i;

		a = new TreeNode();
		a.obj = "a";
		b = new TreeNode();
		b.obj = "b";

		a.addChild(b);
		b.removeFromParent();

		r = a.getChildren();

		i = 0;
		Util.forEach(r,function() {i++;});
		doh.is(i, 0);

		doh.is(b.getParent(), null);
	},

	function addChildOfOtherExistingParent() {
		var a, b, r, i, x;

		a = new TreeNode();
		a.obj = "a";
		b = new TreeNode();
		b.obj = "b";
		a.addChild(b);

		x = new TreeNode();
		x.obj = "x";
		x.addChild(b);



		r = a.getChildren();
		i = 0;
		Util.forEach(r,function() {i++;});
		doh.is(i, 0);



		r = x.getChildren();

		i = 0;
		Util.forEach(r,function() {i++;});
		doh.is(i, 1);

		Util.forEach(r,function(v) {
			doh.is(v.obj, "b");
		});



		doh.is(b.getParent().obj, "x");
	},

	function threeChildren() {
		var p, c1, c2, c3, r, i, c;

		p = new TreeNode();
		p.obj = "p";
		c1 = new TreeNode();
		c1.obj = "c1";
		p.addChild(c1);
		c2 = new TreeNode();
		c2.obj = "c2";
		p.addChild(c2);
		c3 = new TreeNode();
		c3.obj = "c3";
		p.addChild(c3);

		r = p.getChildren();

		i = 0;
		Util.forEach(r,function() {i++;});
		doh.is(i, 3);

		doh.is(r[0].obj, "c1");
		doh.is(r[1].obj, "c2");
		doh.is(r[2].obj, "c3");

		doh.is(c1.getParent().obj, "p");
		doh.is(c2.getParent().obj, "p");
		doh.is(c3.getParent().obj, "p");
	},

	function removeMiddleChild() {
		var p, c1, c2, c3, r, i, c;

		p = new TreeNode();
		p.obj = "p";
		c1 = new TreeNode();
		c1.obj = "c1";
		p.addChild(c1);
		c2 = new TreeNode();
		c2.obj = "c2";
		p.addChild(c2);
		c3 = new TreeNode();
		c3.obj = "c3";
		p.addChild(c3);

		p.removeChild(c2);

		r = p.getChildren();

		doh.is(r.length,2);
		i = 0;
		Util.forEach(r,function() {i++;});
		doh.is(i, 2);

		doh.is(r[0].obj, "c1");
		doh.is(r[1].obj, "c3");

		doh.is(c1.getParent().obj, "p");
		doh.is(c2.getParent(), null);
		doh.is(c3.getParent().obj, "p");
	},

	function addUndefinedChildShouldRaise() {
		var n;
		n = new TreeNode();
		doh.e(Error,n,"addChild",[undefined]);
	},

	function addNullChildShouldRaise() {
		var n;
		n = new TreeNode();
		doh.e(Error,n,"addChild",[null]);
	},

	function removeUndefinedChildShouldRaise() {
		var n;
		n = new TreeNode();
		doh.e(Error,n,"removeChild",[undefined]);
	},

	function removeNullChildShouldRaise() {
		var n;
		n = new TreeNode();
		doh.e(Error,n,"removeChild",[null]);
	}

	]);

})(window.dojo,window.doh);
