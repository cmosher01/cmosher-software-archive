function main() {
	var titleText, title, head, bodyText, ol, body, scriptname, li;



	titleText = document.createTextNode("main");

	title = document.createElement("title");
	title.appendChild(titleText);

	head = document.getElementsByTagName("head")[0];
	head.insertBefore(title,head.firstChild);



	ol = document.createElement("ol");

	body = document.getElementsByTagName("body")[0];
	body.appendChild(ol);

	Util.forEach(document.getElementsByTagName("script"), function(s) {
		scriptname = document.createTextNode(s.src);
		li = document.createElement("li");
		li.appendChild(scriptname);
		ol.appendChild(li);
	});
}
