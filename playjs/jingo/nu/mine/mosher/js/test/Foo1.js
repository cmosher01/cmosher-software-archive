alert("parse Foo1.js");

if (typeof jingo == "undefined") {
	var jingo = {declare:function(){/*dummy*/}};
	throw new Error("This script requires jingo.js from http://code.google.com/p/jingo/");
}

jingo.declare({
	name : 'nu.mine.mosher.js.test.Foo1',
	as : function() {

		nu.mine.mosher.js.test.Foo1 = function(s) {
			alert("run Foo1.js: Foo1");
			this.s = s;
		};

		nu.mine.mosher.js.test.Foo1.prototype.fn = function() {
			return this.s;
		};

	}
});
