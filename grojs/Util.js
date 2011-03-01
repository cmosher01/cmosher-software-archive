function Util() {
	throw new Error("cannot instantiate");
}

Util.prototype.forEach = function(r,fn) {
	var i, v, x;
	x = 0;
	for (i in r) {
		if (r.hasOwnProperty(i)) {
			v = r[i];
			if (v !== undefined) {
				fn(v,x++);
			}
		}
	}
}

Util.prototype.consolodate = function(r) {
	var rr = [];
	Util.prototype.forEach(r, function(v) {
		rr.push(v);
	});
	return rr;
}
