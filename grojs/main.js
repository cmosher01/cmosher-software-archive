function main() {
	var gedcom, titleText, title, head;
	gedcom = null;



	titleText = Util.global.document.createTextNode("Rapp");

	title = Util.global.document.createElement("title");
	title.appendChild(titleText);

	head = Util.global.document.getElementsByTagName("head")[0];
	head.insertBefore(title,head.firstChild);



	var fileref = Util.global.document.createElement("link");
	fileref.rel = "stylesheet";
	fileref.type = "text/css";
	fileref.href = "index.css";
	fileref.media = "screen";
	head.appendChild(fileref);



	Util.global.$.ajaxSetup({
		dataType: "text"
	});

	Util.global.$.get("lib/testged/TGC55C.ged")
	//Util.global.$.get("rapp.ged")
	//Util.global.$.get("RichardsReeves.ged")
		.success(function(gc) {
			gtree = GedcomTree.parse(gc);
			gedcom = new GedcomExtractor(gtree);
		})
		.error(function(s,m,e) {
			alert("Error reading file "+this.url+": "+e);
		});



	Util.global.onresize = function() {
		if (gedcom) {
			gedcom.calc();
		}
	};
}
