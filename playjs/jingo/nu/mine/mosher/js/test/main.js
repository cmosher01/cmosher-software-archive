alert("parse main.js");

if (typeof jingo == "undefined") {
	var jingo = {declare:function(){/*dummy*/}};
	throw new Error("This script requires jingo.js from http://code.google.com/p/jingo/");
}

jingo.declare({
  require: [
    'nu.mine.mosher.js.test.Foo1',
    'nu.mine.mosher.js.test.Foo2'
  ],
  name: 'main',
  as: function() {
	alert("run main.js function");
	var f1, f2;
	f1 = new nu.mine.mosher.js.test.Foo1("abc");
	alert(f1.fn());
	f2 = new nu.mine.mosher.js.test.Foo2("xyz");
	alert(f2.fn());
  }
});
