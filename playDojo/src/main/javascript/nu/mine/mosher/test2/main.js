(function($) {
	"use strict";

	var CLASS = "nu.mine.mosher.test2.main";

	$.provide(CLASS);

	$.require("nu.mine.mosher.test2.Foo2");
	$.require("nu.mine.mosher.test1.Foo1");
	var Foo1 = nu.mine.mosher.test1.Foo1;

	var my = $.declare(CLASS,null,{
	
		constructor: function() {
			alert(this.declaredClass+": constructor");
			this.f1 = null;
			this.f2 = null;
		},

		main: function() {
			alert(this.declaredClass+": main");
			this.f1 = new Foo1("abc");
			alert(this.f1.fn());
			this.f2 = new nu.mine.mosher.test2.Foo2("xyz");
			alert(this.f2.fn());

			var r = ["zero","one","two"];
			delete r[1];
			$.forEach(r,function(i) {
				alert(this.declaredClass+" "+i);
			},this);
		}
	});

	my.doSomething = function() {
		return "something";
	};

	my.CONST = 34;

	my.create = function() {
		return new nu.mine.mosher.test2.main();
	};

})(window.dojo);
