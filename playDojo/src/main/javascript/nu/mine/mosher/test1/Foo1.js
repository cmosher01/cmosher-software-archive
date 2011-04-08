dojo.provide("nu.mine.mosher.test1.Foo1");

dojo.declare("nu.mine.mosher.test1.Foo1", null, {

	constructor: function(s) {
		alert("run Foo1.js: Foo1");
		this.s = s;
	},

	fn: function() {
		return this.s;
	}

});
