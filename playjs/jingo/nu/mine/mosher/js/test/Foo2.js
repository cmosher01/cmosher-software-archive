alert("parse Foo2.js");

if (typeof jingo == "undefined") {
	var jingo = {declare:function(){/*dummy*/}};
	throw new Error("This script requires jingo.js from http://code.google.com/p/jingo/");
}

jingo.declare({
	require : [ 'nu.mine.mosher.js.test.Foo1', ],
	name : 'nu.mine.mosher.js.test.Foo2',
	as : function() {

		nu.mine.mosher.js.test.Foo2 = function(s) {
			alert("run Foo2.js: Foo2");
			this.f1 = new nu.mine.mosher.js.test.Foo1(s);
		};

		nu.mine.mosher.js.test.Foo2.prototype.fn = function() {
			return this.f1.fn();
		};

	}
});
