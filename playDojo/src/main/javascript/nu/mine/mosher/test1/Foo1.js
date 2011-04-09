(function($) {
	"use strict";

	var CLASS = "nu.mine.mosher.test1.Foo1";

	$.provide(CLASS);

	var my = $.declare(CLASS, null, {
	
		constructor: function(s) {
			alert(this.declaredClass+my.SOME_CONST+my.someFunc());
			this.s = s;
		},
	
		fn: function() {
			return this.s;
		}
	
	});

	my.SOME_CONST = ": ";
	
	my.someFunc = function() {
		return "constructor";
	};

	oops = "my bad";
})(window.dojo);
