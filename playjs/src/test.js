/*
class DomBodyOutputLister {
*/
/* instance variables:
	var doc;
	var node;
*/

	/**
	 * a constructor for class DomBodyOutputLister
	 * @param doc
	 * @returns
	 */
	function DomBodyOutputLister(doc) {
		/* local scope variable in constructor */
		var body;

		/**
		 * member variables
		 */
		this.doc = doc;
		this.node = doc.createElement("ul");
	
		/**
		 * validation of arguments/member variables
		 */
		if (this.doc === undefined) {
			throw new Error("doc parameter is required");
		}
	
		/* other operations performed by the constructor */
		body= doc.getElementsByTagName("body")[0];
		body.appendChild(this.node);
	}
	


	// constant
	DomBodyOutputLister.prototype.VERSION2 = "1.2";

	// class property
	// (note that this is actually a property of the constructor (which is a function which is an object)
	DomBodyOutputLister.VERSION = "1.0";

	/**
	 * class method
	 * (no this)
	 */
	DomBodyOutputLister.getVersion = function() {
		return DomBodyOutputLister.VERSION;
	};

	/**
	 * a method
	 * @param s
	 * @returns
	 */
	DomBodyOutputLister.prototype.addLine = function(s) {
		var t = this.doc.createTextNode(s);
	
		var p = this.doc.createElement("li");
		p.appendChild(t);
	
		this.node.appendChild(p);
	
		return p;
	};
/*
} end class DomBodyOutputLister
*/


function dump(obj) {
	var key;
	stdout.addLine(""+(typeof obj)+" "+obj);
	for (key in obj) {
		stdout.addLine(""+(typeof key)+" "+key+" = "+obj[key]);
	}
}
/*
addLine(""+(typeof this)+"--"+this+"--"+this.valueOf(),n);

var fn = function fn() {
	this.foo = function() {
		addLine(""+(typeof this)+"--"+this+"--"+(typeof this.valueOf())+"--"+this.valueOf(),n);
	};
	this.toString = function() {
		return "77";
	};
};

var ff = new fn();
ff.foo();

var book;
book = new Object();
book.title = "JavaScript: The Definitive Guide";
book.xyz = fn;
addLine((typeof book.xyz),n);
delete book.title;
addLine((typeof book.title),n);
*/

var stdout = new DomBodyOutputLister(this.document);

//Define the constructor.
//Note how it initializes the object referred to by "this".
//function Rectangle(width, height)
//{
//	if (this === window) {
//		throw new Error("must use new to construct an object.");
//	}
//	this.width = width;
//	this.height = height;
//	if (this.width === undefined || this.width === null) {
//		throw new Error("must define width");
//	}
//	if (this.height === undefined || this.height === null) {
//		throw new Error("must define height");
//	}
//}
//Invoke the constructor to create two Rectangle objects.
//We pass the width and height to the constructor,
//so it can initialize each new object appropriately.
//var rect1 = new Rectangle(2, 4);
//stdout.addLine(rect1.width);
//stdout.addLine(rect1.height);
//
//var rect3 = new Rectangle(5,6);
//stdout.addLine("here");
//
//stdout.addLine(rect3.width);
//stdout.addLine(rect3.height);

//Rectangle(1,1);
/*
try {
	colours[2] = "red";
} catch (e) {
	dump(e);
}
*/

/*stdout.addLine(window.java);*/




function TypeDef() {
}

TypeDef.getName = function(x) {
  var n;

  if (x === undefined) {
    return "undefined";
  }
  if (x === null) {
    return "null";
  }

  n = Object.prototype.toString.apply(x);
  n = /\[object\s*(.*)\s*\]/.exec(n)[1];

  if (n !== "Object") {
    return n;
  }

  n = x.constructor;
  if (n === undefined || n === null) {
    return "Object";
  }

  n = n.name;
  if (n === undefined || n === null) {
    return "Object";
  }

  return n;
};

TypeDef.define = function(clsName,obj) {
	if (obj.hasOwnProperty("typeName")) {
    throw new TypeError("Object is already a "+obj.typeName);
	}
  if (TypeDef.getName(obj) !== clsName) {
    throw new TypeError("Object must be of class "+clsName);
  }
	obj.typeName = clsName;
};

TypeDef.isStaticCall = function(obj) {
	return !obj.hasOwnProperty("typeName");
};






function Thing() {
  TypeDef.define("Thing",this);
  stdout.addLine("Thing ctor: this is "+Object.prototype.toString.apply(this)+"; "+this.constructor.name+"; typeName="+this.typeName);
  stdout.addLine("Thing ctor: isStaticCall()-->"+TypeDef.isStaticCall(this));
};
Thing.prototype.fn = function() {
  stdout.addLine("Thing fn: this is "+Object.prototype.toString.apply(this)+"; "+this.constructor.name+"; typeName="+this.typeName);
  stdout.addLine("Thing fn: isStaticCall()-->"+TypeDef.isStaticCall(this));
};
Thing.sfn = function() {
  stdout.addLine("Thing sfn: this is "+Object.prototype.toString.apply(this)+"; "+this.constructor.name+"; name="+this.name+"; typeName="+this.typeName);
  stdout.addLine("Thing sfn: isStaticCall()-->"+TypeDef.isStaticCall(this));
};
var fff = Thing.prototype.fn;

stdout.addLine("-----------------------   x = new Thing();");
var x = new Thing();
stdout.addLine("-----------------------   xx = Thing();");
try {
	var xx = Thing();
} catch (e) {
	stdout.addLine(e);
}
stdout.addLine("-----------------------   x.fn();");
x.fn();
stdout.addLine("-----------------------   Thing.prototype.fn();       ????????????");
Thing.prototype.fn();
stdout.addLine("-----------------------   new Thing().fn();");
new Thing().fn();
stdout.addLine("-----------------------   fff();");
fff();
stdout.addLine("-----------------------   Thing.prototype.fn.apply();");
Thing.prototype.fn.apply();
stdout.addLine("-----------------------   Thing.prototype.fn.call();");
Thing.prototype.fn.call();
stdout.addLine("-----------------------   Thing.sfn();");
Thing.sfn();
