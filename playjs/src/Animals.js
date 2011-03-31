var obj;
obj = new Object();



Animal = function(id) {
	if (!arguments.length) {
		return this;
	}
	this.id = id;
};

Animal.prototype.getType = function() {
	return "animal "+this.id+": ";
};




Cat = function(id,fur) {
	Animal.call(this,id);
	this.fur = fur;
};

Cat.prototype = new Animal();
Cat.prototype.constructor = Cat;

Cat.prototype.getType = function() {
	return Animal.prototype.getType.call(this)+"cat";
};

/*
function Cat(id) {
	Cat.superclass.constructor.call(this,id);
}

Cat.superclass = Animal.prototype;
Cat.prototype = new Cat.superclass.constructor();
Cat.prototype.constructor = Cat;

Cat.prototype.getType = function() {
	return Cat.superclass.getType.call(this)+"cat";
};
*/

var a, b, x, c;

a = new Cat("a","black");
b = new Cat("b","white");
x = new Animal("x");

c = a.constructor; // works (c = Cat) (constructor property is inherited from the Cat prototype)
c = Cat; // same as above
c = a.prototype; // doesn't work
c = a.constructor.prototype; // works
c = Cat.prototype; // same as above

c = a.hasOwnProperty("prototype"); // false
c = a.hasOwnProperty("constructor"); // false
c = a.hasOwnProperty("getType"); // false
c = a.hasOwnProperty("id"); // true
c = a.hasOwnProperty("fur"); // true

alert(a.getType());
alert(b.getType());
alert(x.getType());
