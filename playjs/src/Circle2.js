Circle.test_closure = function (r) {
	var d;
	d = r*2;

	Circle.prototype.getRadius = function() {
		return r;
	};
	
	Circle.prototype.getDiameter = function() {
		return d;
	};
};



function Circle(radius) {
	Circle.test_closure(radius);
}





var a;
var b;

a = new Circle(3);
b = new Circle(4);


alert(a.getRadius());
alert(a.getDiameter());

alert(b.getRadius());
alert(b.getDiameter());
