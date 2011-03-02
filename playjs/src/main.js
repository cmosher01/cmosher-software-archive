function main() {
	var titleText = document.createTextNode("Dragger");

	var title = document.createElement("title");
	title.appendChild(titleText);

	var head = document.getElementsByTagName("head")[0];
	head.appendChild(title);

	var fileref = document.createElement("link");
	fileref.setAttribute("rel","stylesheet");
	fileref.setAttribute("type","text/css");
	fileref.setAttribute("href","drag.css");
	head.appendChild(fileref);

	var i1 = new Indi("Linda",50,50);
	var i2 = new Indi("Barry",100,100);
	var i3 = new Indi("James",150,150);

	var i4 = new Indi("Chris",200,200);
	var i5 = new Indi("Nancy",250,250);
	var i6 = new Indi("Casey",300,300);

	var f1 = new Fami(i1,i2,i4,i5);
	var f2 = new Fami(i1,i3,i6);
};
