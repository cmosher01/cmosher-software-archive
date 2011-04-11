(function($) {
	"use strict";

	var CLASS = "nu.mine.mosher.gfx.Selector";

	$.provide(CLASS);

	$.require("nu.mine.mosher.gfx.Point");
	var Point = nu.mine.mosher.gfx.Point;
	$.require("nu.mine.mosher.gfx.Size");
	var Size = nu.mine.mosher.gfx.Size;
	$.require("nu.mine.mosher.gfx.Rect");
	var Rect = nu.mine.mosher.gfx.Rect;
	$.require("nu.mine.mosher.util.Util");
	var Util = nu.mine.mosher.util.Util;

	var Selector = $.declare(CLASS, null, {

		constructor: function(onselect,onselectfinished) {
			Util.global.document.onmousedown =
				Util.eventHandler(this,Selector.prototype.beginDrag);
			this.div = null;
			this.moveProxy = Util.eventHandler(this,Selector.prototype.moveHandler);
			this.upProxy = Util.eventHandler(this,Selector.prototype.upHandler);
			this.start = new Point(0,0);
			this.onselect = onselect;
			this.onselectfinished = onselectfinished;
			this.rect = new Rect(new Point(0,0), new Size(0,0));
		},
		
		setDiv: function() {
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
		},
		
		beginDrag: function(e) {
			if (!Util.leftClick(e)) {
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
		},
		
		moveHandler: function(e) {
			this.pos = Util.mousePos(e);
		
			this.setDiv();
		
			this.onselect(this.rect);
		
			return Util.stopEvent(e);
		},
		
		upHandler: function(e) {
			$(Util.global.document).unbind("mousemove");
			$(Util.global.document).unbind("mouseup");
		
			Util.global.document.body.removeChild(this.div);
		
			this.onselectfinished();
		
			return Util.stopEvent(e);
		}
	});

})(window.dojo);
