function Util() {
	throw new Error("cannot instantiate");
}

Util.forEach = function(r,fn) {
	var i, x, v;
	x = 0;
	for (i = 0; i < r.length; i++) {
		v = r[i];
		if (v !== undefined) {
			fn(v,x++);
		}
	}
}

Util.consolodate = function(r) {
	var rr = [];
	Util.forEach(r, function(v) {
		rr.push(v);
	});
	return rr;
}

Util.getLines = function(s) {
	// unify line terminators
	s = s.replace(/\r\n/g,"\n");
	s = s.replace(/\r/g,"\n");

	// split string into lines
	return s.match(/^.*$/mg);
}

Util.safeStr = function(s) {
	if (s === undefined || s === null) {
		return new String("");
	}
	return new String(s);
}

Util.getTypeName = function(x) {
	var n, m;

	if (x === undefined) {
		return "undefined";
	}
	if (x === null) {
		return "null";
	}

	n = Object.prototype.toString.apply(x);
	n = /\[object\s*(.*)\s*\]/.exec(n)[1];

	if (n !== "Object") {
		return n;
	}

	n = x.constructor;
	if (n === undefined || n === null) {
		return "Object";
	}

	n = n.name;
	if (n === undefined || n === null) {
		n = x.constructor.toString();
		m = /^\s*function\s*(\w+)/.exec(n);
		if (m === null) {
			return "Object";
		}
		return m[1];
	}

	return n;
}

Util.verifyType = function(obj,clsName) {
	if (Util.getTypeName(obj) !== clsName) {
		throw new TypeError(Util.getTypeName(obj)+" must be of class "+clsName);
	}
}

Util.remove = function(e,r) {
	var rr = [];
	Util.forEach(r,function(v) {
		if (v !== e) {
			rr.push(v);
		}
	});
	return rr;
}
