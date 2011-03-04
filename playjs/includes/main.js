alert("parse main.js");

requires("Foo2.js");
requires("Foo1.js");

(function() {
	alert("run main.js function");
	var f1, f2;
	f1 = new Foo1();
	f2 = new Foo2();
})();
