(function($) {
	"use strict";

	var CLASS = "nu.mine.mosher.gro.main";

	$.provide(CLASS);

	$.require("nu.mine.mosher.util.Util");
	var Util = nu.mine.mosher.util.Util;
	$.require("nu.mine.mosher.gedcom.model.GedcomTree");
	var GedcomTree = nu.mine.mosher.gedcom.model.GedcomTree;
	$.require("nu.mine.mosher.gro.GedcomExtractor");
	var GedcomExtractor = nu.mine.mosher.gro.GedcomExtractor;

	var main = $.declare(CLASS, null, {
		constructor: function() {
			throw new Error("cannot instantiate");
		}
	});

	main.main = function() {
		var gedcom, titleText, title, head;
		gedcom = null;
	
	
	
		titleText = $.doc.createTextNode("Rapp");
	
		title = $.doc.createElement("title");
		title.appendChild(titleText);
	
		head = $.doc.getElementsByTagName("head")[0];
		head.insertBefore(title,head.firstChild);
	
	
	
		var fileref = $.doc.createElement("link");
		fileref.rel = "stylesheet";
		fileref.type = "text/css";
		fileref.href = "index.css";
		fileref.media = "screen";
		head.appendChild(fileref);
	
	
	
	
		//"lib/testged/TGC55C.ged"
		//"RichardsReeves.ged"
		$.xhrGet({
			url: "/home/chris/workspace/grojo/src/main/rapp.ged",
			load: function(gc) {
				gtree = GedcomTree.parse(gc);
				gedcom = new GedcomExtractor(gtree);
			},
			error: function(e) {
				alert("Error reading file "+this.url+": "+e);
			}
		});
	
		$.connect($.doc,"onresize",gedcom,"calc");
	
	};
})(window.dojo);
