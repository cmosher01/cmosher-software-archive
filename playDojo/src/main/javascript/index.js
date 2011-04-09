(function($) {
	"use strict";

	$.require("nu.mine.mosher.test2.main");

	$.addOnLoad(function(){
		  var main = nu.mine.mosher.test2.main.create();
		  main.main();
		});
})(window.dojo);
