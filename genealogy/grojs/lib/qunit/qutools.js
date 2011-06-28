$(function() {
	QUnit.log = function(details) {
		if (window.console && window.console.log) {
		   window.console.log(details.result+": expected "+details.expected+", was actually "+details.actual);
		}
	};
});
