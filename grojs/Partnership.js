function Partnership(gid,husb,wife,rchil) {
	var outerthis;

	Util.verifyType(this,"Partnership");

	this.gid = gid;
	this.husb = husb;
	this.wife = wife;
	this.rchil = rchil;



	if (this.husb !== null) {
		this.husb.addSpouseIn(this);
	}

	if (this.wife !== null) {
		this.wife.addSpouseIn(this);
	}

	outerthis = this;
	Util.forEach(this.rchil, function(c) {
		c.setChildIn(outerthis);
	});
}
