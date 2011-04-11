(function($,doh) {
	"use strict";

	$.provide("nu.mine.mosher.util.tests.tests");

	try {
		$.require("nu.mine.mosher.util.tests.Util");
	} catch(e) {
		doh.debug(e);
	}

})(window.dojo, window.doh);
