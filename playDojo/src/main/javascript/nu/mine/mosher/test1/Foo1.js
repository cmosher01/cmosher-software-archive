(function() {

	var CLASS = "nu.mine.mosher.test1.Foo1";

	dojo.provide(CLASS);

	var CTOR = dojo.declare(CLASS, null, {
	
		constructor: function(s) {
			alert(this.declaredClass+": constructor");
			this.s = s;
		},
	
		fn: function() {
			return this.s;
		}
	
	});

})();
