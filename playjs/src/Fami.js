function Fami(h,w/*children*/) {
	var ia;
	var ic;
	var child;
	var div;

	this.husb = h;
	this.wife = w;
	this.chil = new Array();
	for (ia = 2; ia < arguments.length; ia++) {
		child = arguments[ia];
		this.chil[this.chil.length] = child;
		child.childInFami = this;
	}


	this.husb.spouseInFami[this.husb.spouseInFami.length] = this;
	this.wife.spouseInFami[this.wife.spouseInFami.length] = this;

	this.divLeft = document.createElement("div");
	this.divLeft.style.position = "absolute";
	document.body.appendChild(this.divLeft);

	this.divRight = document.createElement("div");
	this.divRight.style.position = "absolute";
	document.body.appendChild(this.divRight);

	this.divChild = new Array();
	for (ic = 0; ic < this.chil.length; ic++) {
		div = document.createElement("div");
		this.divChild[this.divChild.length] = div;
		div.style.position = "absolute";
		document.body.appendChild(div);
	}

	if (this.divChild.length > 0) {
		this.divChildConn = document.createElement("div");
		this.divChildConn.style.position = "absolute";
		this.divChildConn.style.width = 3+"px";
		this.divChildConn.style.borderLeft = "solid black 1px";
		document.body.appendChild(this.divChildConn);
	} else {
		this.divChildConn = null;
	}

	this.calc();

}

Fami.prototype.calc = function() {
	var mx;
	var my;
	var left;
	var right;
	var rt;
	var lt;
	var lr;
	var rl;
	var leftUnder;
	var ic;
	var child;
	var div;
	var cy;
	var cx;

	if (this.husb.left() < this.wife.left()) {
		left = this.husb;
		right = this.wife;
	} else {
		left = this.wife;
		right = this.husb;
	}

	rt = right.top()+right.height()/2-2;
	lt = left.top()+left.height()/2-2;
	lr = left.right();
	rl = right.left();

	mx = (lr+rl-1)/2;

	my = Number.MAX_VALUE;
	for (ic = 0; ic < this.chil.length; ic++) {
		child = this.chil[ic];
		cy = child.top();
		if (cy < my) {
			my = cy;
		}
	}
	my -= 10;

//	window.defaultStatus = "lt: "+lt+"; rt: "+rt+"; lr: "+lr;

	leftUnder = (rt < lt);

	this.divLeft.style.left = lr+"px";
	this.divLeft.style.width = (mx-lr)+"px";
	this.divLeft.style.borderRight = "solid black 1px";
	if (leftUnder) {
		this.divLeft.style.top = rt+"px";
		this.divLeft.style.height = (lt-rt)+"px";
		this.divLeft.style.borderBottom = "double black 3px";
		this.divLeft.style.borderTop = "none";
	} else {
		this.divLeft.style.top = lt+"px";
		this.divLeft.style.height = (rt-lt)+"px";
		this.divLeft.style.borderTop = "double black 3px";
		this.divLeft.style.borderBottom = "none";
	}

	this.divRight.style.left = mx+"px";
	this.divRight.style.width = (rl-mx)+"px";
	if (leftUnder) {
		this.divRight.style.top = rt+"px";
		this.divRight.style.height = (lt-rt)+"px";
		this.divRight.style.borderTop = "double black 3px";
		this.divRight.style.borderBottom = "none";
	} else {
		this.divRight.style.top = lt+"px";
		this.divRight.style.height = (rt-lt)+"px";
		this.divRight.style.borderBottom = "double black 3px";
		this.divRight.style.borderTop = "none";
	}

	for (ic = 0; ic < this.chil.length; ic++) {
		child = this.chil[ic];
		div = this.divChild[ic];

		div.style.top = my+"px";
		div.style.height = child.top()-my+"px";
		div.style.borderTop = "solid black 1px";
		div.style.borderBottom = "none";
		cx = (child.left()+child.right()-1)/2;
		if (cx < mx) {
			div.style.left = cx+"px";
			div.style.width = mx-cx+"px";
			div.style.borderLeft = "solid black 1px";
			div.style.borderRight = "none";
		} else {
			div.style.left = mx+"px";
			div.style.width = cx-mx+"px";
			div.style.borderRight = "solid black 1px";
			div.style.borderLeft = "none";
		}
	}
	if (this.divChildConn) {
		this.divChildConn.style.left = mx+"px";
		if (rt < my) {
			this.divChildConn.style.top = rt+"px";
			this.divChildConn.style.height = my-rt+"px";
		} else {
			this.divChildConn.style.top = my+"px";
			this.divChildConn.style.height = rt-my+"px";
		}
	}
}

function con(d,vx,vy,hx,hy,sh,sv) {
	
}
