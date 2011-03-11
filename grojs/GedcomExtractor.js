/**
 * @fileoverview
 * Defines the {@link GedcomExtractor} class.
 */

/**
 * @class
 * Extracts needed data from a {@link GedcomTree}.
 * @requires Partnership
 * @requires Person
 * @requires GedcomEvent
 * @requires GedcomDateParser
 * @requires Point
 * @requires Util
 * 
 * @constructor
 * @param {GedcomTree} gedcomtree tree to extract data from
 * @return new {@link GedcomExtractor}
 * @type GedcomExtractor
 */
function GedcomExtractor(gedcomtree) {
	Util.verifyType(this,"GedcomExtractor");
	/**
	 * tree to extract from
	 * @private
	 * @type GedcomTree
	 */
	this.t = gedcomtree;
	Util.verifyType(this.t,"GedcomTree");

	/**
	 * map of IDs to {@link Person}s.
	 * @private
	 * @type Object
	 */
	this.mperson = {};

	/**
	 * map of IDs to {@link Partnership}s.
	 * @private
	 * @type Object
	 */
	this.mpartnership = {};

	this.extract();
}

/**
 * Calculates every {@link Partnership}.
 */
GedcomExtractor.prototype.calc = function() {
	Util.forEach(this.mpartnership, function(p) {
		p.calc();
	});
};

/**
 * Extracts the data from the tree.
 * @private
 */
GedcomExtractor.prototype.extract = function() {
	var rchil, that;
	rchil = this.t.getRoot().getChildren();
	that = this;
	Util.forEach(rchil, function(node) {
		if (node.line.getTag() === "INDI") {
			that.mperson[node.line.getID()] = that.extractPerson(node);
		}
	});
	Util.forEach(rchil, function(node) {
		if (node.line.getTag() === "FAM") {
			that.mpartnership[node.line.getID()] = that.extractParnership(node);
		}
	});
};

/**
 * Extracts one {@link Person} from the given INDI node
 * @private
 * @param {TreeNode} indi
 * @return new {@link Person}
 * @type Person
 */
GedcomExtractor.prototype.extractPerson = function(indi) {
	var that, nam, xy, m, revt, line;

	that = this;

	nam = "[unknown]";
	revt = [];
	xy = new Point(0,0);
	Util.forEach(indi.getChildren(), function(node) {
		if (node.line.getTag() === "NAME") {
			nam = node.line.getVal().replace(/\//g,"");
		} else if (node.line.getTag() === "_XY") {
			m = /(\d+)\s+(\d+)/.exec(node.line.getVal());
			if (m !== null) {
				xy = new Point(m[1],m[2]);
			}
		} else if (GedcomTag.isIndiEvent(node.line.getTag())) {
			revt.push(that.extractEvent(node));
		}
	});

	line = indi.line;
	return new Person(line.getID(),nam,xy,revt);
};


/**
 * Extracts one {@link Partnership} from the given FAM node
 * @private
 * @param {TreeNode} fam
 * @return new {@link Partnership}
 * @type Partnership
 */
GedcomExtractor.prototype.extractParnership = function(fam) {
	var that, husb, wife, rchil, revt, line;
	that = this;
	husb = null;
	wife = null;
	rchil = [];
	revt = [];
	Util.forEach(fam.getChildren(), function(node) {
		if (node.line.getTag() === "HUSB") {
			husb = that.mperson[node.line.getPointer()];
		} else if (node.line.getTag() === "WIFE") {
			wife = that.mperson[node.line.getPointer()];
		} else if (node.line.getTag() === "CHIL") {
			rchil.push(that.mperson[node.line.getPointer()]);
		} else if (GedcomTag.isFamEvent(node.line.getTag())) {
			revt.push(that.extractEvent(node));
		}
	});
	line = fam.line;
	return new Partnership(line.getID(),husb,wife,rchil,revt);
};

/**
 * Extracts one {@link GedcomEvent} from the given event node
 * @private
 * @param {TreeNode} evt
 * @return new {@link GedcomEvent}
 * @type GedcomEvent
 */
GedcomExtractor.prototype.extractEvent = function(evt) {
	var that, typ, gdate, place;
	that = this;
	gdate = null;
	place = null;
	typ = this.extractEventName(evt);
	Util.forEach(evt.getChildren(), function(node) {
		if (node.line.getTag() === "DATE") {
			gdate = that.extractDate(node.line.getVal());
		} else if (node.line.getTag() === "PLAC") {
			place = node.line.getVal();
		}
	});
	return new GedcomEvent(typ,gdate,place);
};

/**
 * Extracts (constructs) the event-name for the given event node.
 * @param evt
 * @private
 * @param {TreeNode} evt
 * @return event name
 * @type String
 */
GedcomExtractor.prototype.extractEventName = function(evt) {
	var nam, val;
	nam = "";
	val = "";

	if (evt.line.getTag() === "EVEN") {
		Util.forEach(evt.getChildren(), function(node) {
			if (node.line.getTag() === "TYPE") {
				nam = node.line.getVal();
			}
		});
	}
	if (!nam) {
		nam = GedcomTag.getEventName(evt.line.getTag());
		val = evt.line.getVal();
		if (val) {
			nam += ": "+val;
		}
	}
	return nam;
};

/**
 * Extracts a {@link DatePeriod} from a given GEDCOM date string.
 * @param {String} s GEDCOM date string to parse
 * @return {@link DatePeriod}
 * @type DatePeriod
 */
GedcomExtractor.prototype.extractDate = function(s) {
	var r, ymd, rng;
	r = null;
	try {
		r = Util.global.GedcomDateParser.parse(s);
	} catch (e) {
		r = null;
	}
	if (YMD.isParsedYMD(r)) {
		ymd = YMD.fromParserResult(r);
		rng = new DateRange(ymd);
		return new DatePeriod(rng,rng);
	}
	if (DateRange.isParsedDateRange(r)) {
		rng = DateRange.fromParserResult(r);
		return new DatePeriod(rng,rng);
	}
	if (DatePeriod.isParsedDatePeriod(r)) {
		return DatePeriod.fromParserResult(r);
	}
	return DatePeriod.UNKNOWN;
};
