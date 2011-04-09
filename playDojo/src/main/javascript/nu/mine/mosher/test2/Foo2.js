(function() {

	var CLASS = "nu.mine.mosher.test2.Foo2";

	dojo.provide(CLASS);

	dojo.require("nu.mine.mosher.test1.Foo1");

	var CTOR = dojo.declare(CLASS, null, {

		// ctor
		constructor: function(s) {
			alert(this.declaredClass+": constructor");
			this.f1 = new nu.mine.mosher.test1.Foo1(s);
		},
	
		// methods
		fn: function() {
			return this.f1.fn();
		}
	});

})();
