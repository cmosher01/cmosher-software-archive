function Dragger(dragee,onmovedHandler,shadow) {
	this.onmoved = onmovedHandler;

	if (!(parseInt(dragee.style.left) >= 0)) {
		dragee.style.left = "1px";
	}
	if (!(parseInt(dragee.style.top) >= 0)) {
		dragee.style.top = "1px";
	}
	dragee.style.position = "absolute";

	dragee.dragger = this;

	dragee.onmousedown = function(evt) {
		var dragee = this;

        if (!evt) {
        	evt = Util.global.event;  // IE Event Model
        }

		// Figure out where the element currently is
		// The element must have left and top CSS properties in a style attribute
		// Also, we assume they are set using pixel units
		var x = parseInt(dragee.style.left);
		var y = parseInt(dragee.style.top);

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
		} else {  // IE 4 Event Model
			// In IE 4 we can't use attachEvent(), so assign the event handlers
			// directly after storing any previously assigned handlers, so they 
			// can be restored. Note that this also relies on event bubbling.
			var oldmovehandler = Util.global.document.onmousemove;
			var olduphandler = Util.global.document.onmouseup;
			Util.global.document.onmousemove = moveHandler;
			Util.global.document.onmouseup = upHandler;
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
			dragee.style.left = (e.clientX - deltaX) + "px";
			dragee.style.top = (e.clientY - deltaY) + "px";

			if (shadow) {
				shadow.style.left = (e.clientX - deltaX) + "px";
				shadow.style.top = (e.clientY - deltaY) + "px";
			}

			dragee.dragger.onmoved.onmoved();

			// And don't let anyone else see this event.
			if (e.stopPropagation) {
				e.stopPropagation(); // DOM Level 2
			} else {
				e.cancelBubble = true; // IE
			}
			return false;
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
			} else {  // IE 4 Event Model
				Util.global.document.onmouseup = olduphandler;
				Util.global.document.onmousemove = oldmovehandler;
			}
	
			dragee.dragger.onmoved.onmovedfinish();

			// And don't let the event propagate any further.
			if (e.stopPropagation) {
				e.stopPropagation(); // DOM Level 2
			} else {
				e.cancelBubble = true; // IE
			}
			return false;
		}
	};
}
