function Selector() {
	Util.global.document.onmousedown =
		Util.eventHandler(this,Selector.prototype.beginDrag);
	this.div = null;
	this.moveProxy = Util.eventHandler(this,Selector.prototype.moveHandler);
	this.upProxy = Util.eventHandler(this,Selector.prototype.upHandler);
	this.start = new Point(0,0);
}

Selector.prototype.setDiv = function() {
	this.div.style.left = Util.px(Math.min(this.start.getX(),this.pos.getX()));
	this.div.style.top = Util.px(Math.min(this.start.getY(),this.pos.getY()));
	this.div.style.width = Util.px(Math.abs(this.pos.getX()-this.start.getX()));
	this.div.style.height = Util.px(Math.abs(this.pos.getY()-this.start.getY()));
};

Selector.prototype.beginDrag = function(evt) {
	this.div = Util.createHtmlElement("div");
	this.div.className = "selector";
	this.div.style.position = "absolute";
	this.div.style.border = "solid 1px";
	this.pos = new Point(evt.clientX,evt.clientY);
	this.start = this.pos;
	Util.global.document.body.appendChild(this.div);
	this.setDiv();

	if (Util.global.document.addEventListener) {  // DOM Level 2 Event Model
		// Register capturing event handlers
		Util.global.document.addEventListener("mousemove", this.moveProxy, true);
		Util.global.document.addEventListener("mouseup", this.upProxy, true);
	} else if (Util.global.document.attachEvent) {  // IE 5+ Event Model
		// In the IE event model, we can't capture events, so these handlers
		// are triggered only if the event bubbles up to them.
		// This assumes that there aren't any intervening elements that
		// handle the events and stop them from bubbling.
		Util.global.document.attachEvent("onmousemove", this.moveProxy);
		Util.global.document.attachEvent("onmouseup", this.upProxy);
	}

	// We've handled this event. Don't let anybody else see it.  
	if (evt.stopPropagation) {
		evt.stopPropagation(); // DOM Level 2
	} else {
		evt.cancelBubble = true; // IE
	}

	// Now prevent any default action.
	if (evt.preventDefault) {
		evt.preventDefault(); // DOM Level 2
	} else {
		evt.returnValue = false; // IE
	}

	return false;
};

Selector.prototype.moveHandler = function(e) {
	this.pos = new Point(e.clientX,e.clientY);
	this.setDiv();

	// And don't let anyone else see this event.
	if (e.stopPropagation) {
		e.stopPropagation(); // DOM Level 2
	} else {
		e.cancelBubble = true; // IE
	}
	if (evt.preventDefault) {
		evt.preventDefault(); // DOM Level 2
	} else {
		evt.returnValue = false; // IE
	}
	return false;
};

Selector.prototype.upHandler = function(e) {
	if (Util.global.document.removeEventListener) {  // DOM Event Model
		Util.global.document.removeEventListener("mouseup", this.upProxy, true);
		Util.global.document.removeEventListener("mousemove", this.moveProxy, true);
	} else if (Util.global.document.detachEvent) {  // IE 5+ Event Model
		Util.global.document.detachEvent("onmouseup", this.upProxy);
		Util.global.document.detachEvent("onmousemove", this.moveProxy);
	}

	Util.global.document.body.removeChild(this.div);

	// And don't let the event propagate any further.
	if (e.stopPropagation) {
		e.stopPropagation(); // DOM Level 2
	} else {
		e.cancelBubble = true; // IE
	}
	return false;
};
