/*
 * @licstart  The following is the entire license notice for the JavaScript code in this page.
 *
 * Copyright (C) 2012, by Christopher Alan Mosher, Shelton, CT.
 *
 * The JavaScript code in this page is free software: you can
 * redistribute it and/or modify it under the terms of the GNU
 * General Public License (GNU GPL) as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option)
 * any later version.  The code is distributed WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU GPL for more details.
 *
 * As additional permission under GNU GPL version 3 section 7, you
 * may distribute non-source (e.g., minimized or compacted) forms of
 * that code without the copy of the GNU GPL normally required by
 * section 4, provided you include this license notice and a URL
 * through which recipients can access the Corresponding Source.
 *
 * @licend  The above is the entire license notice for the JavaScript code in this page.
 */

/**
 * @fileoverview
 * Defines the {@link GedcomEvent} class.
 */

define(["dojo/_base/declare"],
function(declare) {

	"use strict";

	return declare(null, {
		constructor: function(auth,titl,publ,text) {
			this.auth = auth;
			this.titl = titl;
			this.publ = publ;
			this.text = text;
		},
		getAuthor: function() {
			return this.auth;
		},
		getTitle: function() {
			return this.titl;
		},
		getPublication: function() {
			return this.publ;
		},
		getText: function() {
			return this.text;
		},
		/* quick and dirty HTML string */
		getHtml: function() {
			return this.auth+".<br><i><b>"+this.titl+".</b></i><br>"+this.publ+".<hr>"+this.text;
		}
	});

});
