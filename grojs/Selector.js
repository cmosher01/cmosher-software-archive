var $ = Util.global.jQuery;

function Selector(onselect,onselectfinished) {
	Util.global.document.onmousedown =
		Util.eventHandler(this,Selector.prototype.beginDrag);
	this.div = null;
	this.moveProxy = Util.eventHandler(this,Selector.prototype.moveHandler);
	this.upProxy = Util.eventHandler(this,Selector.prototype.upHandler);
	this.start = new Point(0,0);
	this.onselect = onselect;
	this.onselectfinished = onselectfinished;
	this.rect = new Rect(new Point(0,0), new Size(0,0));
}

Selector.prototype.setDiv = function() {
	this.rect = new Rect(
			new Point(
				Math.min(this.start.getX(),this.pos.getX()),
				Math.min(this.start.getY(),this.pos.getY())
			),
			new Size(
				Math.abs(this.pos.getX()-this.start.getX()),
				Math.abs(this.pos.getY()-this.start.getY())
			)
		);
	this.div.style.left = Util.px(this.rect.getPos().getX());
	this.div.style.top = Util.px(this.rect.getPos().getY());
	this.div.style.width = Util.px(this.rect.getSize().getWidth());
	this.div.style.height = Util.px(this.rect.getSize().getHeight());
};

Selector.prototype.beginDrag = function(e) {
	if (e.button != 0) {
		return true;
	}
	if (e.clientX >= Util.global.document.documentElement.clientWidth || e.clientY >= Util.global.document.documentElement.clientHeight) {
		return true;
	}

	this.start = this.pos = Util.mousePos(e);

	this.div = Util.createHtmlElement("div");
	this.div.className = "selector";
	this.div.style.position = "absolute";
	Util.global.document.body.appendChild(this.div);

	this.setDiv();

	this.onselect(this.rect);

	$(Util.global.document).mousemove(this.moveProxy);
	$(Util.global.document).mouseup(this.upProxy);

	return Util.stopEvent(e);
};

Selector.prototype.moveHandler = function(e) {
	this.pos = Util.mousePos(e);

	this.setDiv();

	this.onselect(this.rect);

	return Util.stopEvent(e);
};

Selector.prototype.upHandler = function(e) {
	$(Util.global.document).unbind("mousemove");
	$(Util.global.document).unbind("mouseup");

	Util.global.document.body.removeChild(this.div);

	this.onselectfinished();

	return Util.stopEvent(e);
};
