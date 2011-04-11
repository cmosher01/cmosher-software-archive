(function($) {
	"use strict";

	var CLASS = "nu.mine.mosher.gfx.Dragger";

	$.provide(CLASS);

	$.require("nu.mine.mosher.gfx.Point");
	var Point = nu.mine.mosher.gfx.Point;
	$.require("nu.mine.mosher.gfx.Size");
	var Size = nu.mine.mosher.gfx.Size;
	$.require("nu.mine.mosher.util.Util");
	var Util = nu.mine.mosher.util.Util;

	var Dragger = $.declare(CLASS, null, {

		constructor: function(dragee, dragHandler, userArg) {
			this.dragHandler = dragHandler;
			this.userArg = userArg;
		
			dragee.onmousedown = Util.eventHandler(this,Dragger.prototype.beginDrag);
		
			this.origin = new Point(0,0);
		},
		
		beginDrag = function(e) {
			if (!Util.leftClick(e)) {
				return true;
			}
		
			jQuery(Util.global.document).mouseup(Util.eventHandler(this,Dragger.prototype.upHandler));
			jQuery(Util.global.document).mousemove(Util.eventHandler(this,Dragger.prototype.moveHandler));
		
			this.origin = Util.mousePos(e);
		
			this.dragHandler.onBeginDrag(this.userArg);
		
			return Util.stopEvent(e);
		},
		
		/**
		 * This is the handler that captures mousemove events when an element
		 * is being dragged. It is responsible for moving the element.
		 * @param {Event} e event (but not for IE)
		 * @return <code>false</code> to not propagate event for IE
		 * @type Boolean
		 **/
		moveHandler = function(e) {
			var p = Util.mousePos(e);
		
			this.dragHandler.onDrag(new Size(p.getX()-this.origin.getX(),p.getY()-this.origin.getY()));
		
			return Util.stopEvent(e);
		},
		
		/**
		 * This is the handler that captures the final mouseup event that
		 * occurs at the end of a drag
		 * @param {Event} e event (but not for IE)
		 * @return <code>false</code> to not propagate event for IE
		 * @type Boolean
		 **/
		upHandler = function(e) {
			jQuery(Util.global.document).unbind("mousemove");
			jQuery(Util.global.document).unbind("mouseup");
		
			this.dragHandler.onEndDrag();
		
			this.origin = new Point(0,0);
		
			return Util.stopEvent(e);
		}
	});

})(window.dojo);
