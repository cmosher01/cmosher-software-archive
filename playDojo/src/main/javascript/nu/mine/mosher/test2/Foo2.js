(function($) {
	"use strict";

	var CLASS = "nu.mine.mosher.test2.Foo2";

	$.provide(CLASS);

	$.require("nu.mine.mosher.test1.Foo1");

	var my = $.declare(CLASS, null, {

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

})(window.dojo);
