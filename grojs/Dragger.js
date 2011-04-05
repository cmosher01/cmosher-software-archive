function Dragger(dragee,onmovedHandler,shadow) {
	dragee.dragger = this;

	dragee.style.position = "absolute";
	dragee.dragger.onmoved = onmovedHandler;

	this.begindrag = function(evt) {
		if (evt.button != 0) {
			return true;
		}
		// Figure out where the element currently is
		// The element must have left and top CSS properties in a style attribute
		// Also, we assume they are set using pixel units
		var x = parseInt(dragee.style.left,10);
		var y = parseInt(dragee.style.top,10);

		// Compute the distance between that point and the mouse-click
		// The nested moveHandler function below needs these values
		var deltaX = evt.clientX - x;
		var deltaY = evt.clientY - y;
		
		// Register the event handlers that will respond to the mousemove events
		// and the mouseup event that follow this mousedown event.  
		if (Util.global.document.addEventListener) {  // DOM Level 2 Event Model
			// Register capturing event handlers
			Util.global.document.addEventListener("mousemove", moveHandler, true);
			Util.global.document.addEventListener("mouseup", upHandler, true);
		} else if (Util.global.document.attachEvent) {  // IE 5+ Event Model
			// In the IE event model, we can't capture events, so these handlers
			// are triggered only if the event bubbles up to them.
			// This assumes that there aren't any intervening elements that
			// handle the events and stop them from bubbling.
			Util.global.document.attachEvent("onmousemove", moveHandler);
			Util.global.document.attachEvent("onmouseup", upHandler);
		}

		/**
		* This is the handler that captures mousemove events when an element
		* is being dragged. It is responsible for moving the element.
		* @param {Event} e event (but not for IE)
		* @return <code>false</code> to not propagate event for IE
		* @type Boolean
		**/
		function moveHandler(e) {
			if (!e) {
				e = Util.global.event;  // IE Event Model
			}
			// Move the element to the current mouse position, adjusted as
			// necessary by the offset of the initial mouse-click.
			dragee.style.left = Util.px(e.clientX - deltaX);
			dragee.style.top = Util.px(e.clientY - deltaY);

			if (shadow) {
				shadow.style.left = Util.px(e.clientX - deltaX);
				shadow.style.top = Util.px(e.clientY - deltaY);
			}

			dragee.dragger.onmoved.onmoved();

			return Util.stopEvent(e);
		}
		
		/**
		* This is the handler that captures the final mouseup event that
		* occurs at the end of a drag
		* @param {Event} e event (but not for IE)
		* @return <code>false</code> to not propagate event for IE
		* @type Boolean
		**/
		function upHandler(e) {
			if (!e) {
				e = Util.global.event;  // IE Event Model
			}
	
			// Unregister the capturing event handlers.
			if (Util.global.document.removeEventListener) {  // DOM Event Model
				Util.global.document.removeEventListener("mouseup", upHandler, true);
				Util.global.document.removeEventListener("mousemove", moveHandler, true);
			} else if (Util.global.document.detachEvent) {  // IE 5+ Event Model
				Util.global.document.detachEvent("onmouseup", upHandler);
				Util.global.document.detachEvent("onmousemove", moveHandler);
			}
	
			dragee.dragger.onmoved.onmovedfinish();

			return Util.stopEvent(e);
		}

		return Util.stopEvent(evt);
	};

	dragee.onmousedown = Util.eventHandler(this,this.begindrag);
}
