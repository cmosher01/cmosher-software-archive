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

Util.prototype.safeStr = function(s) {
	if (s === undefined || s === null) {
		return new String("");
	}
	return new String(s);
}

Util.prototype.getClassName = function(x) {
	var c;
	switch (x) {
		case null: return "null";
		case undefined: return "undefined";
	}
	c = x.constructor;
	switch (c) {
	case null:
	case undefined:
		return typeof x;
	}
	switch (c.name) {
	case null:
	case undefined:
		return typeof x;
	}
	return c.name;
}
