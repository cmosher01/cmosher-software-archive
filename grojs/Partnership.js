function Partnership(gid,husb,wife,rchil) {
	var ic;
	var outer_this = this;

	Util.verifyType(this,"Partnership");

	this.gid = gid;
	this.husb = husb;
	this.wife = wife;
	this.rchil = rchil;



	if (this.husb !== null) {
		this.husb.addSpouseIn(this);
	}

	if (this.wife !== null) {
		this.wife.addSpouseIn(this);
	}

	Util.forEach(this.rchil, function(c) {
		c.setChildIn(outer_this);
	});



	this.divLeft = Partnership.createDiv();
	this.divRight = Partnership.createDiv();

	this.divChild = [];
	Util.forEach(rchil, function() {
		outer_this.divChild.push(Partnership.createDiv());
	});

	if (this.divChild.length > 0) {
		this.divChildConn = Partnership.createDiv();
	} else {
		this.divChildConn = null;
	}

	this.calc();
}



Partnership.createDiv = function() {
	var div;
	div = document.createElement("div");
	div.style.position = "absolute";
	document.body.appendChild(div);
	return div;
};

Partnership.getChildLine = function() {
	return "solid orange 1px";
};

Partnership.getSpouseLine = function() {
	return "double orange 4px";
};

Partnership.prototype.calc = function() {
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
	Util.forEach(this.rchil, function(chil) {
	});
	for (ic = 0; ic < this.rchil.length; ic++) {
		child = this.rchil[ic];
		cy = child.top();
		if (cy < my) {
			my = cy;
		}
	}
	my -= 10;

	leftUnder = (rt < lt);

	this.divLeft.style.left = lr+"px";
	this.divLeft.style.width = (mx-lr)+"px";
	this.divLeft.style.borderRight = Partnership.getChildLine();
	if (leftUnder) {
		this.divLeft.style.top = rt+"px";
		this.divLeft.style.height = (lt-rt)+"px";
		this.divLeft.style.borderBottom = Partnership.getSpouseLine();
		this.divLeft.style.borderTop = "none";
	} else {
		this.divLeft.style.top = lt+"px";
		this.divLeft.style.height = (rt-lt)+"px";
		this.divLeft.style.borderTop = Partnership.getSpouseLine();
		this.divLeft.style.borderBottom = "none";
	}

	this.divRight.style.left = mx+"px";
	this.divRight.style.width = (rl-mx)+"px";
	if (leftUnder) {
		this.divRight.style.top = rt+"px";
		this.divRight.style.height = (lt-rt)+"px";
		this.divRight.style.borderTop = Partnership.getSpouseLine();
		this.divRight.style.borderBottom = "none";
	} else {
		this.divRight.style.top = lt+"px";
		this.divRight.style.height = (rt-lt)+"px";
		this.divRight.style.borderBottom = Partnership.getSpouseLine();
		this.divRight.style.borderTop = "none";
	}

	for (ic = 0; ic < this.rchil.length; ic++) {
		child = this.rchil[ic];
		div = this.divChild[ic];

		div.style.top = my+"px";
		div.style.height = child.top()-my+"px";
		div.style.borderTop = Partnership.getChildLine();
		div.style.borderBottom = "none";
		cx = (child.left()+child.right()-1)/2;
		if (cx < mx) {
			div.style.left = cx+"px";
			div.style.width = mx-cx+"px";
			div.style.borderLeft = Partnership.getChildLine();
			div.style.borderRight = "none";
		} else {
			div.style.left = mx+"px";
			div.style.width = cx-mx+"px";
			div.style.borderRight = Partnership.getChildLine();
			div.style.borderLeft = "none";
		}
	}
	if (this.divChildConn) {
		this.divChildConn.style.left = mx+"px";
		this.divChildConn.style.width = 3+"px";
		this.divChildConn.style.borderLeft = Partnership.getChildLine();
		if (rt < my) {
			this.divChildConn.style.top = rt+"px";
			this.divChildConn.style.height = my-rt+"px";
		} else {
			this.divChildConn.style.top = my+"px";
			this.divChildConn.style.height = rt-my+"px";
		}
	}
};
