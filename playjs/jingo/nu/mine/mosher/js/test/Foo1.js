if (window.jingo == null) {
	jingo = {declare: function(x) {/*dummy*/}};
	throw new Error("requires jingo");
}

jingo.declare({
	name : 'nu.mine.mosher.js.test.Foo1',
	as : function() {

		var Foo1;
		Foo1 = function(s) {
			alert("run Foo1.js: Foo1");
			this.s = s;
		};
		nu.mine.mosher.js.test.Foo1 = Foo1;

		Foo1.prototype.fn = function() {
			return this.s;
		};

	}
});
