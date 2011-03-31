var MODULE_A = (function (my) {
	var X = 3;
	my.a = function() { return X; };
	return my;
}(MODULE_A || {}));


var MODULE_A = (function (my) {
	var FOO = 4;
	my.b = function() { return FOO; };
	return my;
}(MODULE_A || {}));





// alert(MODULE_A.a());
// alert(MODULE_A.b());





// NAMESPACE:

var Animals = (function(my) {
	my.Dog = function(name) {
		this.name = name;
	};

	my.Dog.prototype.speak = function() {
		alert("Woof! " + this.name);
	};

	return my;
})(Animals || {});

var Animals = (function(my) {
	my.Cat = function(name) {
		this.name = name;
	};

	my.Cat.prototype.speak = function() {
		alert("Meow! " + this.name);
	};

	return my;
})(Animals || {});

//Then you can make lots of dogs like this:
var fido = new Animals.Dog("fido");
fido.speak(); // => alerts ‘Woof! fido’

var fluffy = new Animals.Cat("fluffy");
fluffy.speak(); // => alerts ‘Woof! rover’




// NAMESPACE alternate:


if (!this.Animals2) {
	Animals2 = {};
}

Animals2.Dog = function(name) {
	this.name = name;
};

Animals2.Dog.prototype.speak = function() {
	alert("Woof! " + this.name);
};

if (!this.Animals2) {
	Animals2 = {};
}

Animals2.Cat = function(name) {
	this.name = name;
};

Animals2.Cat.prototype.speak = function() {
	alert("Meow! " + this.name);
};
fido = new Animals2.Dog("fido");
fido.speak(); // => alerts ‘Woof! fido’

fluffy = new Animals2.Cat("fluffy");
fluffy.speak(); // => alerts ‘Woof! rover’
