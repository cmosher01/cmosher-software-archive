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
		c.addChildIn(outer_this);
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

Partnership.getColor = function() {
	return "#5E665F";
};

Partnership.getChildLine = function() {
	return "solid "+Partnership.getColor()+" 1px";
};

Partnership.getSpouseLine = function() {
	return "double "+Partnership.getColor()+" 4px";
};

Partnership.prototype.calc = function() {
	var that = this;
	var mx;
	var my;
	var left;
	var right;
	var rmy;
	var lmy;
	var lmx;
	var rmx;
	var leftUnder;
	var ic;
	var child;
	var div;
	var cy;
	var cx;


	if (this.husb && this.wife) {
		if (this.husb.midx() < this.wife.midx()) {
			left = this.husb;
			right = this.wife;
		} else {
			left = this.wife;
			right = this.husb;
		}

		lmx = left.midx();
		rmx = right.midx();

		lmy = left.midy();
		rmy = right.midy();
	}
	if (this.wife && !this.husb) {
		lmx = this.wife.midx();
		rmx = lmx + 100;;

		lmy = this.wife.midy();
		rmy = lmy;
	}
	if (this.husb && !this.wife) {
		lmx = this.husb.midx();
		rmx = lmx + 100;;

		lmy = this.husb.midy();
		rmy = lmy;
	}
	if (!this.wife && !this.husb) {
		lmx = rmx = lmy = rmy = 0; // TODO
	}

	// mid x pos between parents
	mx = (lmx+rmx)/2;


	// find topmost child
	my = Number.MAX_VALUE;
	Util.forEach(this.rchil, function(chil) {
		cy = chil.top();
		if (cy < my) {
			my = cy;
		}
	});
	my -= 10; // distance of child bar above children

	// is left parent below right parent?
	leftUnder = (rmy < lmy);



// DIVs

	this.divLeft.style.left = Util.px(lmx);
	this.divLeft.style.width = Util.px(mx-lmx);
	this.divLeft.style.borderLeft = "none";
	this.divLeft.style.borderRight = "none"; // why none?
	if (leftUnder) {
		this.divLeft.style.top = Util.px(rmy);
		this.divLeft.style.height = Util.px(lmy-rmy);
		this.divLeft.style.borderBottom = Partnership.getSpouseLine();
		this.divLeft.style.borderTop = "none";
	} else {
		this.divLeft.style.top = Util.px(lmy);
		this.divLeft.style.height = Util.px(rmy-lmy);
		this.divLeft.style.borderTop = Partnership.getSpouseLine();
		this.divLeft.style.borderBottom = "none";
	}



	this.divRight.style.left = Util.px(mx);
	this.divRight.style.width = Util.px(rmx-mx);
	this.divRight.style.borderLeft = Partnership.getChildLine();
	this.divRight.style.borderRight = "none";
	if (leftUnder) {
		this.divRight.style.top = Util.px(rmy);
		this.divRight.style.height = Util.px(lmy-rmy);
		this.divRight.style.borderTop = Partnership.getSpouseLine();
		this.divRight.style.borderBottom = "none";
	} else {
		this.divRight.style.top = Util.px(lmy);
		this.divRight.style.height = Util.px(rmy-lmy);
		this.divRight.style.borderBottom = Partnership.getSpouseLine();
		this.divRight.style.borderTop = "none";
	}



	for (ic = 0; ic < this.rchil.length; ic++) {
		child = this.rchil[ic];
		div = this.divChild[ic];

		div.style.top = Util.px(my);
		div.style.height = Util.px(child.top()-my);
		div.style.borderTop = Partnership.getChildLine();
		div.style.borderBottom = "none";
		cx = (child.left()+child.right()-1)/2;
		if (cx < mx) {
			div.style.left = Util.px(cx);
			div.style.width = Util.px(mx-cx);
			div.style.borderLeft = Partnership.getChildLine();
			div.style.borderRight = "none";
		} else {
			div.style.left = Util.px(mx);
			div.style.width = Util.px(cx-mx);
			div.style.borderRight = Partnership.getChildLine();
			div.style.borderLeft = "none";
		}
	}
	if (this.divChildConn) {
		this.divChildConn.style.left = Util.px(mx);
		this.divChildConn.style.width = Util.px(3);
		this.divChildConn.style.borderLeft = Partnership.getChildLine();
		if (rmy < my) {
			this.divChildConn.style.top = Util.px(rmy);
			this.divChildConn.style.height = Util.px(my-rmy);
		} else {
			this.divChildConn.style.top = Util.px(my);
			this.divChildConn.style.height = Util.px(rmy-my);
		}
	}
};
