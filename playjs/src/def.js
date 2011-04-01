// global variable thisvariabledoesnotexist is not even declared
if (window.thisvariabledoesnotexist && window.thisvariabledoesnotexist.fn) {
	window.thisvariabledoesnotexist.fn();
}

// global variable foo is declared but not initialized (so is undefined)
var foo;
if (window.foo && window.foo.fn) {
	window.foo.fn();
}

//global variable foo is declared and initialized to null
var foo = null;
if (window.foo && window.foo.fn) {
	window.foo.fn();
}
