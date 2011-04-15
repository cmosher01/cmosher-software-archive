(function($) {
	"use strict";

	var CLASS = "nu.mine.mosher.gro.main";

	$.provide(CLASS);

	$.require("nu.mine.mosher.gedcom.model.GedcomTree");
	var GedcomTree = nu.mine.mosher.gedcom.model.GedcomTree;
	$.require("nu.mine.mosher.gro.GedcomExtractor");
	var GedcomExtractor = nu.mine.mosher.gro.GedcomExtractor;

	$.declare(CLASS, null, {
		constructor: function() {
			throw new Error("cannot instantiate");
		}
	});

	nu.mine.mosher.gro.main.main = function() {
		var gedcom, head;

		gedcom = null;

		// remove any existing title from the document
		title = $.query("html head title").forEach($.destroy);

		// add our title to the document
		head = $.query("html head")[0];
		$.create("title",{innerHTML:"GRO Javascript"},head,"first");

		// add our style-sheet to the document
		$.create("link",{rel:"stylesheet",type:"text/css",href:"index.css",media:"screen"},head);




		//"lib/testged/TGC55C.ged"
		//"RichardsReeves.ged"
		$.xhrGet({
			url: "../rapp.ged",
			load: function(gc) {
				var gtree = GedcomTree.parse(gc);
				gedcom = new GedcomExtractor(gtree);
			},
			error: function(e) {
				alert("Error reading file "+this.url+": "+e);
			}
		});

		$.connect($.global,"onresize",function(e) {
			if (gedcom != null) {
				gedcom.calc();
			}
		});

	};
})(window.dojo);
