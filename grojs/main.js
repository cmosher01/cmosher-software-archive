function main() {
	var gedcom, titleText, title, head, bodyText, ol, body, scriptname, li;



	titleText = document.createTextNode("main");

	title = document.createElement("title");
	title.appendChild(titleText);

	head = document.getElementsByTagName("head")[0];
	head.insertBefore(title,head.firstChild);



	var fileref = document.createElement("link");
	fileref.rel = "stylesheet";
	fileref.type = "text/css";
	fileref.href = "index.css";
	fileref.media = "screen";
	head.appendChild(fileref);





	$.ajaxSetup({
		dataType: "text"
	});

	$.get("rapp.ged")
		.success(function(gc) {
			gtree = GedcomTree.parse(gc);
			gedcom = new GedcomExtractor(gtree);
		})
		.error(function(s,m,e) {
			alert("Error reading file "+this.url+": "+e);
		});
}
