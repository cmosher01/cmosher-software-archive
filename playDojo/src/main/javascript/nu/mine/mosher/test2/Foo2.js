dojo.provide("nu.mine.mosher.test2.Foo2");

dojo.require("nu.mine.mosher.test1.Foo1");

dojo.declare("nu.mine.mosher.test2.Foo2", null, {

	// ctor
	constructor: function(s) {
		alert("run Foo2.js: Foo2");
		this.f1 = new nu.mine.mosher.test1.Foo1(s);
	},

	// methods
	fn: function() {
		return this.f1.fn();
	}
});
