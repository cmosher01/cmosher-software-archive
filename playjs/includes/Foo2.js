alert("parse Foo2.js");

requires("Foo1.js");

function Foo2() {
	alert("run Foo2.js: Foo2");
	var f1 = new Foo1();
}
