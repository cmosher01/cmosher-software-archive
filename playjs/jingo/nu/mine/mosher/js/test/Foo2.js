if (window.jingo == null) {
	jingo = {declare: function(x) {/*dummy*/}};
	throw new Error("requires jingo");
}
jingo.declare({
	require : [ 'nu.mine.mosher.js.test.Foo1' ],
	name : 'nu.mine.mosher.js.test.Foo2',
	as : function() {

		// shortcuts for imports
		var Foo1 = nu.mine.mosher.js.test.Foo1;

		// ctor
		var Foo2;
		Foo2 = function(s) {
			alert("run Foo2.js: Foo2");
			this.f1 = new Foo1(s);
		};
		nu.mine.mosher.js.test.Foo2 = Foo2;

		// methods
		Foo2.prototype.fn = function() {
			return this.f1.fn();
		};
	}
});
