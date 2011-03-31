/* constructor */

function Circle(radius) {
	/* instance variables */
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
	return Circle.PI * Circle.square(this.getRadius());
};

Circle.prototype.getCircumference = function() {
	return Circle.PI * this.getDiameter();
};

/* (member) factory methods (instead of mutators) */

Circle.prototype.expand = function() {
	return new Circle(this.getRadius()*Circle.expansionFactor);
};

/* (static) factory methods */

Circle.createUnitCircle = function() {
	return new Circle(1);
};

/* static methods */

Circle.square = function(a) {
	return a*a;
};

/* constants */

Circle.PI = 3.14159265;

Circle.expansionFactor = 3;



/*
var a;

a = new Circle(3);

alert(a.getArea());
alert(a.getCircumference());
a.expand(); // has no effect on a
alert(a.getArea());
alert(a.getCircumference());
a = a.expand();
alert(a.getArea());
alert(a.getCircumference());
*/















/* subclass */


/* constructor */

function ColorCircle(radius,color) {
	Circle.call(this,radius);
	this.color = color;
}

ColorCircle.prototype = new Circle(0);
var whatctor = ColorCircle.prototype.constructor;
ColorCircle.prototype.constructor = ColorCircle;

ColorCircle.prototype.getColor = function() {
	return this.color;
};




var c;
c = new ColorCircle(5,"red");
alert(c.getColor());
alert(c.getDiameter());
