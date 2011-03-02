function __getClass(object) {
  return Object.prototype.toString.call(object)
    .match(/^\[object\s(.*)\]$/)[1];
};

function Rect(x,y) {
	this.x = x;
	this.y = y;
}

function main() {
	var r;
	r = new Rect(3,4);
	alert(__getClass(r));
}
