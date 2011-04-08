dojo.provide("nu.mine.mosher.test2.main");

dojo.require("nu.mine.mosher.test2.Foo2");
dojo.require("nu.mine.mosher.test1.Foo1");

dojo.declare("nu.mine.mosher.test2.main",null,{
	aaabbbccc: "chris", // !!! this goes into the prototype !!!!:  nu.mine.mosher.test2.main.prototype.aaabbbccc  USUALLY NOT WHAT YOU WANT

	constructor: function() {
		this.f1 = null;
		this.f2 = null;
	},

	main: function() {
		alert("run main.js function");
		this.f1 = new nu.mine.mosher.test1.Foo1("abc");
		alert(this.f1.fn());
		this.f2 = new nu.mine.mosher.test2.Foo2("xyz");
		alert(this.f2.fn());
	}
});
