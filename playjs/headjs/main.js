alert("parse main.js");

depends("Foo2.js");
depends("Foo1.js");

function main() {
	alert("run main.js function");
	var f1, f2;
	f1 = new Foo1();
	f2 = new Foo2();
}
