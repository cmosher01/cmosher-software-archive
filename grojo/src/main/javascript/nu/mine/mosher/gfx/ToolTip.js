(function($) {
	"use strict";

	var CLASS = "nu.mine.mosher.gfx.ToolTip";

	$.provide(CLASS);

	$.require("nu.mine.mosher.gfx.Point");
	var Point = nu.mine.mosher.gfx.Point;
	$.require("nu.mine.mosher.gfx.Size");
	var Size = nu.mine.mosher.gfx.Size;
	$.require("nu.mine.mosher.util.Util");
	var Util = nu.mine.mosher.util.Util;

	var ToolTip = $.declare(CLASS, null, {

		constructor: function(tipper,tipHTML) {
			this.tipHTML = tipHTML;
			this.div = null;

			$.connect(tipper,"onmouseover",this,"show");
			$.connect(tipper,"onmouseout",this,"hide");
		},

		show: function(e) {
			this.moveConnection = $.connect($.doc,"onmousemove",this,"moveToCursor");
			this.div = $.create("div",{innerHTML:this.tipHTML,className:"tooltip",style:{position:"absolute",zIndex:20}},$.doc.body);
			this.moveToCursor(e);
			return $.stopEvent(e);
		},

		hide: function(e) {
			$.destroy(this.div);
			$.disconnect(this.moveConnection);
			return $.stopEvent(e);
		},

		moveToCursor: function(e) {
			var p = Point.fromBrowserEvent(e);
			this.div.style.left = Util.px(p.getX()+10);
			this.div.style.top = Util.px(p.getY()+10);
			return $.stopEvent(e);
		}
	});

})(window.dojo);
