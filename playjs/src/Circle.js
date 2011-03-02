/* constructor */

function Circle(radius) {
	/* prevent missing "new" operator */
	if (this === window) {
		throw new Error("must use \"new\" operator to construct an object.");
	}

	/* instance variables (but not mutated) */
	this.radius = radius;
	this.diameter = radius*2;
}

/* simple accessor methods */

Circle.prototype.getRadius = function() {
	return this.radius;
};

Circle.prototype.getDiameter = function() {
	return this.diameter;
};

/* computed accessor methods */

Circle.prototype.getArea = function() {
	return this.PI() * this.square(this.getRadius());
};

Circle.prototype.getCircumference = function() {
	return this.PI() * this.getDiameter();
};

/* factory methods (instead of mutators) */

Circle.prototype.expand = function() {
	return new Circle(this.getRadius()*this.getExpansionFactor());
};

/* static methods (same as computed accessor methods (don't reference "this," but not enforced) */

Circle.prototype.square = function(a) {
	return a*a;
};

/* constants */

Circle.prototype.PI = function() {
	return 3.14159265;
};

/* configurable constants */

Circle.prototype.getExpansionFactor = function() {
	return 3;
};




var a;

a =  Circle(3);

alert(a.getArea());
alert(a.getCircumference());
a.expand(); // has no effect on a
alert(a.getArea());
alert(a.getCircumference());
a = a.expand();
alert(a.getArea());
alert(a.getCircumference());
