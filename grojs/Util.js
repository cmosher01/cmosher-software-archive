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

Util.prototype.getLines = function(s) {
	// unify line terminators
	s = s.replace(/\r\n/g,"\n");
	s = s.replace(/\r/g,"\n");

	// split string into lines
	return s.match(/^.*$/mg);
}
