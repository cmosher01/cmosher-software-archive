var r, i;


r = [];

r.push("a");
r.push("b");
r.push("c");
r.push("d");
r.pop();

for (i in r) {
	if (r[i]==="b") {
		delete r[i];
	}
}

for (i in r) {
	alert(r[i]);
}
