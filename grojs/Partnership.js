function Partnership(gid,husb,wife,rchil) {
	var ic;
	var that = this;

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
		c.addChildIn(that);
	});



	this.divLeft = Partnership.createDiv();
	this.divRight = Partnership.createDiv();

	this.divChild = [];
	Util.forEach(rchil, function() {
		that.divChild.push(Partnership.createDiv());
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
	if (!Partnership.lineColor) {
		// pull body color from stylesheet (and cache it)
		if(document.defaultView && document.defaultView.getComputedStyle) {
			Partnership.lineColor = document.defaultView.getComputedStyle(document.body,"").getPropertyValue("color");
		} else if(document.body.currentStyle) {
			Partnership.lineColor = document.body.currentStyle["color"];
		}
	}
	return Partnership.lineColor;
};

Partnership.getChildLine = function() {
	return "solid "+Partnership.getColor()+" 1px";
};

Partnership.getSpouseLine = function() {
	var h = 2*Partnership.getMarBarHalfHeight();
	return "double "+Partnership.getColor()+" "+h+"px";
};

Partnership.getMarBarHalfHeight = function() {
	return 2;
};

Partnership.getMarChildDistance = function() {
	return 10;
};

Partnership.prototype.calc = function() {
	var that = this;
	var mx;
	var my;
	var rmy;
	var lmy;
	var lmx;
	var rmx;
	var ic;
	var child;
	var crect;
	var div;
	var cy;
	var cx;
	var rectLeft, rectRight, rtemp;



	// find topmost child
	my = Number.MAX_VALUE;
	Util.forEach(this.rchil, function(chil) {
		cy = chil.getRect().getTop();
		if (cy < my) {
			my = cy;
		}
	});
	my -= 15; // 15 is distance of child bar above children



	// get two parent rects (calc if missing)
	if (this.husb && this.wife) {
		rectLeft = this.husb.getRect();
		rectRight = this.wife.getRect();
	} else if (this.wife && !this.husb) {
		rectLeft = this.wife.getRect();
		rectRight = Partnership.phantomSpouse(rectLeft);
	} else if (this.husb && !this.wife) {
		rectLeft = this.husb.getRect();
		rectRight = Partnership.phantomSpouse(rectLeft);
	} else if (!this.wife && !this.husb) {
		rectLeft = rectRight = null;
	}

	if (rectLeft) {
		// make sure rectLeft is to the LEFT of rectRight
		if (rectRight.getMidX() < rectLeft.getMidX()) {
			rtemp = rectLeft;
			rectLeft = rectRight;
			rectRight = rtemp;
		}

		lmx = rectLeft.getMidX();
		rmx = rectRight.getMidX();
		mx = Partnership.getDescenderX(rectLeft,rectRight);

		lmy = rectLeft.getMidY()-Partnership.getMarBarHalfHeight();
		rmy = rectRight.getMidY()-Partnership.getMarBarHalfHeight();
		by = Math.max(rmy,lmy); // bottom of parents

		Partnership.setRect(this.divLeft,lmx,null,mx,null,rmy,null,lmy,Partnership.getSpouseLine());
		Partnership.setRect(this.divRight,mx,Partnership.getChildLine(),rmx,null,rmy,Partnership.getSpouseLine(),lmy,null);

	} else {
		// children with no parents
		mx = this.rchil[0].getRect().getMidX(); // mid x of any child
		by = my;
	}

	for (ic = 0; ic < this.rchil.length; ic++) {
		child = this.rchil[ic];
		crect = child.getRect();
		div = this.divChild[ic];

		Partnership.setRect(div,crect.getMidX(),Partnership.getChildLine(),mx,null,my,Partnership.getChildLine(),crect.getTop(),null);
	}

	if (this.divChildConn) {
		Partnership.setRect(this.divChildConn,mx,Partnership.getChildLine(),mx+3,null,by,null,my,null);
	}
};

Partnership.phantomSpouse = function(rect) {
	return new Rect(
		new Point(rect.getRight()+2*Partnership.getMarChildDistance()+1,rect.getTop()),
		new Size(3,rect.getHeight()));
};

Partnership.getDescenderX = function(rectLeft,rectRight) {
/*
	Nominal case: descender bar is just to the right of the LEFT spouse. But, if 
	the distance between spouses is too small to fit a nice-sized marriage bar 
	into, so move the descender bar out just to the right of the RIGHT spouse.
*/
	return (rectLeft.getRight() + 2*Partnership.getMarChildDistance() < rectRight.getLeft() ? rectLeft : rectRight)
		.getRight()+Partnership.getMarChildDistance();
};

// sets div's left/right pos/borders
Partnership.setX = function(div,xLeft,borderLeft,xRight,borderRight) {
	var t;
	var style = div.style;

	if (xRight < xLeft) {
		t = xRight;
		xRight = xLeft;
		xLeft = t;
		t = borderLeft;
		borderLeft = borderRight;
		borderRight = t;
	}
	style.left = Util.px(xLeft);
	style.borderLeft = borderLeft ? borderLeft : "none";
	style.width = Util.px(Math.max(0,xRight-xLeft));
	style.borderRight = borderRight ? borderRight : "none";
};

// sets div's top/bottom pos/borders
Partnership.setY = function(div,yTop,borderTop,yBottom,borderBottom) {
	var t;
	var style = div.style;

	if (yBottom < yTop) {
		t = yBottom;
		yBottom = yTop;
		yTop = t;
		t = borderTop;
		borderTop = borderBottom;
		borderBottom = t;
	}
	style.top = Util.px(yTop);
	style.borderTop = borderTop ? borderTop : "none";
	style.height = Util.px(Math.max(0,yBottom-yTop));
	style.borderBottom = borderBottom ? borderBottom : "none";
};

Partnership.setRect = function(div,x1,borderX1,x2,borderX2,y1,borderY1,y2,borderY2) {
	Partnership.setX(div,x1,borderX1,x2,borderX2);
	Partnership.setY(div,y1,borderY1,y2,borderY2);
};
