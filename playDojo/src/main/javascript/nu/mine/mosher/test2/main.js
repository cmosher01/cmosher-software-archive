(function() {

	var CLASS = "nu.mine.mosher.test2.main";

	dojo.provide(CLASS);

	dojo.require("nu.mine.mosher.test2.Foo2");
	dojo.require("nu.mine.mosher.test1.Foo1");

	var CTOR = dojo.declare(CLASS,null,{
	
		constructor: function() {
			alert(this.declaredClass+": constructor");
			this.f1 = null;
			this.f2 = null;
		},

		main: function() {
			alert(this.declaredClass+": main");
			this.f1 = new nu.mine.mosher.test1.Foo1("abc");
			alert(this.f1.fn());
			this.f2 = new nu.mine.mosher.test2.Foo2("xyz");
			alert(this.f2.fn());
		}
	});

	CTOR.doSomething = function() {
		return "something";
	};

	CTOR.CONST = 34;

	CTOR.create = function() {
		return new nu.mine.mosher.test2.main();
	};

})();
