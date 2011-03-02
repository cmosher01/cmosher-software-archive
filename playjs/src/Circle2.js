function test_closure(r) {
	Circle.prototype.getRadius = function() {
		return r;
	};
	
	Circle.prototype.getDiameter = function() {
		return r*2;
	};
}



function Circle(radius) {
	/* prevent missing "new" operator */
	if (this === window) {
		throw new Error("must use \"new\" operator to construct an object.");
	}
	test_closure(radius);
}





var a;
a = new Circle(3);
alert(a.getRadius.toSource());
