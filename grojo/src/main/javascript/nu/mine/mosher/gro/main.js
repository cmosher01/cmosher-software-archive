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
		head = $.byId("head");
		$.create("title",{innerHTML:"GRO Javascript"},head,"first");

		// add digit theme style-sheet
		//$.create("link",{rel:"stylesheet",type:"text/css",href:"dijit/themes/tundra/tundra.css",media:"screen"},$.query("html head")[0]);
		// add our style-sheet to the document
		$.create("link",{rel:"stylesheet",type:"text/css",href:"index.css",media:"screen"},head);

//		$.create("input",{id:"import",type:"file"},$.doc.body);

		$.create("div",{id:"dropline"},$.doc.body);

//		nu.mine.mosher.gro.main.menu();


		//"lib/testged/TGC55C.ged"
		//"RichardsReeves.ged"
		$.xhrGet({
			url: "../rapp.ged",
			load: function(gc) {
				var gtree = GedcomTree.parse(gc);
				gedcom = new GedcomExtractor(gtree,$.byId("dropline"));
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

//	nu.mine.mosher.gro.main.menu = function() {
//        var pMenu = new dijit.Menu({
//        	targetNodeIds: ["html"]
//        });
//        pMenu.addChild(new dijit.MenuItem({
//            label: "Do nothing",
//            onClick: function() {
//            	
//            }
//        }));
//        pMenu.startup();
//	};
})(window.dojo);
