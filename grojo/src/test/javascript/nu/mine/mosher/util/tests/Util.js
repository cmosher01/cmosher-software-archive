(function($,doh) {
	"use strict";

	var SUITE = "nu.mine.mosher.util.tests.Util";

	$.provide(SUITE);

	$.require("nu.mine.mosher.util.Util");
	var Util = nu.mine.mosher.util.Util;



	function ChrisMosherUtilInternalTestClass() { /*do nothing*/ }



	doh.register(SUITE,[

	function forEachNominal() {
		var r, rr;

		r = ["a","b","c"];

		rr = [];
		Util.forEach(r,function(v) {
			rr.push(v);
		});

		doh.is(rr,r);
	},

	function forEachNull() {
		var r, rr;

		r = ["a",null,"c"];

		rr = [];
		Util.forEach(r,function(v) {
			rr.push(v);
		});

		doh.is(rr,r);
	},

	function forEachDeleted() {
		var r, rr;

		r = ["a","b","c"];
		delete r[1];

		rr = [];
		Util.forEach(r,function(v) {
			rr.push(v);
		});

		doh.is(rr,["a","c"]);
	},

	function forEachSparse() {
		var r, rr;

		r = [];
		r[0] = "a";
		r[10000] = "b";

		rr = [];
		Util.forEach(r,function(v) {
			rr.push(v);
		});

		doh.is(rr,["a","b"]);
	},


	function forEachObjectProperty() {
		var obj, rr;
		obj = {
			a: "a",
			b: "b"
		};
		rr = [];
		Util.forEach(obj,function(v) {
			rr.push(v);
		});

		doh.t((rr[0]=="a"&&rr[1]=="b") || (rr[0]=="b"&&rr[1]=="a"));
	},




	function consolodateNominal() {
		var r, rr;

		r = ["a","b","c"];
		delete r[1];

		rr = Util.consolodate(r);
		doh.is(rr,["a","c"]);
	},





	function removeNominal() {
		var r;

		r = ["a","b","c"];
		delete r[1];
		doh.isNot(r,["a","c"]);
		doh.is(r,["a",Util.undefined(),"c"]);

		r = ["a","b","c"];
		r = Util.remove("b",r);
		doh.is(r,["a","c"]);
		doh.isNot(r,["a",Util.undefined(),"c"]);
	},




	function classNameOfClassicOOObject() {
		doh.is(Util.getTypeName(new ChrisMosherUtilInternalTestClass()),"ChrisMosherUtilInternalTestClass");
	},

	function classNameOfObject() {
		doh.is(Util.getTypeName(new Object()),"Object");
	},

	function classNameOfString() {
		doh.is(Util.getTypeName(new String("testing")),"String");
	},

	function classNameOfStringLiteral() {
		doh.is(Util.getTypeName("testing"),"String");
	},

	function classNameOfArray() {
		doh.is(Util.getTypeName(new Array(1,2,3)),"Array");
	},

	function classNameOfArrayLiteral() {
		doh.is(Util.getTypeName([1,2,3]),"Array");
	},

	function classNameOfnumber() {
		doh.is(Util.getTypeName(new Number(3.14)),"Number");
	},

	function classNameOfnumber() {
		doh.is(Util.getTypeName(3.14),"Number");
	},

	function classNameOfnumberProperty() {
		doh.is(Util.getTypeName(window.length),"Number");
	},

	function classNameOfBoolean() {
		doh.is(Util.getTypeName(new Boolean(true)),"Boolean");
	},

	function classNameOfBooleanLiteral() {
		doh.is(Util.getTypeName(true),"Boolean");
	},

	function classNameOfNull() {
		doh.is(Util.getTypeName(null),"null");
	},

	function classNameOfUndefined() {
		doh.is(Util.getTypeName(Util.undefined()),"undefined");
	},

	function classNameOfOmittedArgument() {
		doh.is(Util.getTypeName(),"undefined");
	},

	function classNameOfCastratedObject() {
		var x;
		x = new ChrisMosherUtilInternalTestClass();
		x.constructor = null;
		doh.is(Util.getTypeName(x),"Object");
	},

	function classNameOfCorruptedObject() {
		var x;
		x = new ChrisMosherUtilInternalTestClass();
		x.constructor = {foo:"bar"};
		doh.is(Util.getTypeName(x),"Object");
	},

	function classNameOfCorruptedObject2() {
		var x;
		x = new ChrisMosherUtilInternalTestClass();
		x.constructor = [1,2,3];
		doh.is(Util.getTypeName(x),"Object");
	},

	function classNameOfDate() {
		doh.is(Util.getTypeName(new Date()),"Date");
	},

	function classNameOfRegExpObject() {
		doh.is(Util.getTypeName(new RegExp(/.*/g)),"RegExp");
	},

	function classNameOfRegExpLiteral() {
		doh.is(Util.getTypeName(/.*/g),"RegExp");
	},

	function classNameOfFunctionObject() {
		doh.is(Util.getTypeName(function(){/*do nothing*/}),"Function");
	},

	function classNameOfCastratedFunctionObject() {
		var x = function(){/*do nothing*/};
		x.constructor = null;
		doh.is(Util.getTypeName(x),"Function");
	},

	function classNameOfCastratedRegExp() {
		var x = /.*/g;
		x.constructor = null;
		doh.is(Util.getTypeName(x),"RegExp");
	},

	function classNameOfObjectType() {
		doh.is(Util.getTypeName(Object),"Function");
	},

	function classNameOfMathType() {
		doh.is(Util.getTypeName(Math),"Math");
	},

	function classNameOfDateType() {
		doh.is(Util.getTypeName(Date),"Function");
	},

	function classNameOfWindow() {
		doh.is(Util.getTypeName(window),"Window");
	},

	function classNameOfMaliciousCorruption() {
		var x = new ChrisMosherUtilInternalTestClass();
		x.constructor = {name:"Foo"};
		doh.is(Util.getTypeName(x),"Foo"); // can't fix this?
	},

//	function classNameOfArguments() {
//		doh.is(Util.getTypeName(arguments),"Arguments");
//	},

	function classNameOfCallee() {
		doh.is(Util.getTypeName(arguments.callee),"Function");
	},

	function safestrNominal() {
		doh.is(Util.safeStr("Chris"),"Chris");
	},

	function safestrUndefined() {
		doh.is(Util.safeStr(Util.undefined()),"");
	},

	function safestrMissing() {
		doh.is(Util.safeStr(),"");
	},

	function safestrNull() {
		doh.is(Util.safeStr(null),"");
	},


	function digintNominal() {
		doh.is(Util.digint(68,3),"068");
	},

	function digintNoneAdded() {
		doh.is(Util.digint(68,2),"68");
	},

	function digintTooBig() {
		doh.is(Util.digint(68,1),"68");
	},

	function digintMultiDigitZero() {
		doh.is(Util.digint(0,7),"0000000");
	},

	function digintZero() {
		doh.is(Util.digint(0,1),"0");
	},

	function digintNegativeNominal() {
		doh.is(Util.digint(-27,5),"-00027");
	},

	function digintFractional() {
		doh.is(Util.digint(-27.47,5),"-00027");
	},

	function digintFractionalRoundUp() {
		doh.is(Util.digint(-27.58,5),"-00028");
	},

	function undefinedFunction() {
		doh.is(typeof Util.undefined(),"undefined");
	},

	function NaNFUnction() {
		doh.is(typeof Util.NaN(),"number");
		doh.t(isNaN(Util.NaN()));
	},

	function InfinityFunction() {
		doh.is(typeof Util.Infinity(),"number");
		doh.is(Util.Infinity(),1/0);
	}
]);

})(window.dojo,window.doh);
