function Dragger(dragee,onmovedHandler) {
	this.onmoved = onmovedHandler;
	dragee.className = "drag";
	if (!(parseInt(dragee.style.left) >= 0)) {
		dragee.style.left = "1px";
	}
	if (!(parseInt(dragee.style.top) >= 0)) {
		dragee.style.top = "1px";
	}
	dragee.style.position = "absolute";

	dragee.dragger = this;

	dragee.onmousedown = function(event) {
		var dragee = this;

		// Figure out where the element currently is
		// The element must have left and top CSS properties in a style attribute
		// Also, we assume they are set using pixel units
		var x = parseInt(dragee.style.left);
		var y = parseInt(dragee.style.top);

		// Compute the distance between that point and the mouse-click
		// The nested moveHandler function below needs these values
		var deltaX = event.clientX - x;
		var deltaY = event.clientY - y;
		
		// Register the event handlers that will respond to the mousemove
		// and mouseup events that follow this mousedown event. Note that
		// these are registered as capturing event handlers on the document.
		// These event handlers remain active while the mouse button remains
		// pressed and are removed when the button is released.
		dragee.ownerDocument.addEventListener("mousemove", moveHandler, true);
		dragee.ownerDocument.addEventListener("mouseup", upHandler, true);
		
		// We've handled this event. Don't let anybody else see it.
		event.stopPropagation();
		event.preventDefault();
		
		/**
		* This is the handler that captures mousemove events when an element
		* is being dragged. It is responsible for moving the element.
		**/
		function moveHandler(event) {
			//var x = Math.min(Math.max(0,event.clientX - deltaX),clientWidth()-dragee.scrollWidth);
			//var y = Math.min(Math.max(0,event.clientY - deltaY),clientHeight()-dragee.scrollHeight);
			var x = Math.max(0,event.clientX - deltaX);
			var y = Math.max(0,event.clientY - deltaY);
			
			// Move the element to the current mouse position, adjusted as
			// necessary by the offset of the initial mouse-click
			dragee.style.left = x + "px";
			dragee.style.top = y + "px";
			
			// And don't let anyone else see this event
			event.stopPropagation();
			dragee.dragger.onmoved.onmoved();
		};
		
		/**
		* This is the handler that captures the final mouseup event that
		* occurs at the end of a drag
		**/
		function upHandler(event) {
			// Unregister the capturing event handlers
			dragee.ownerDocument.removeEventListener("mouseup", upHandler, true);
			dragee.ownerDocument.removeEventListener("mousemove", moveHandler, true);
			// And don't let the event propagate any further
			event.stopPropagation();
		};
	};
}
