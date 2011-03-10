start
	=	date_value

date_value
	=	ymd:date
	/	per:date_period
	/	rng:date_range
	/	ymd:date_approx { ymd.approx=true; return ymd; }
	/	ymd:date_int
	/	str:date_phrase

date_int
	=	"INT" S ymd:date S date_phrase { return ymd; }

date_phrase
	=	"(" phrase:[^)]* ")" { return phrase.join(""); }

date_period
	=	"FROM" S f:date S "TO" S t:date { return { from:f, to:t }; }
	/	"FROM" S f:date                 { return { from:f       }; }
	/	                  "TO" S t:date { return {         to:t }; }

date_range
	=	"BET" S a:date S "AND" S b:date { return { after:a, before:b }; }
	/	"AFT" S a:date                  { return { after:a           }; }
	/	                 "BEF" S b:date { return {          before:b }; }

date_approx
	=	"ABT" S ymd:date { return ymd; }
	/	"CAL" S ymd:date { return ymd; }
	/	"EST" S ymd:date { return ymd; }

date
	=	"@#D" cal:"GREGORIAN" "@" S ymd:date_greg { return ymd; }
	/	"@#D" cal:"JULIAN"    "@" S ymd:date_greg { ymd.julian = true; return ymd; }
	/	"@#D" cal:"HEBREW"    "@" S ymd:date_hebr { return null; /* not yet implemented */ }
	/	"@#D" cal:"FRENCH R"  "@" S ymd:date_fren { return null; /* not yet implemented */ }
	/	"@#D" cal:other_cal   "@" S phrase:.*     { return cal+": "+phrase.join(""); }
	/	                            ymd:date_greg { return ymd; }

other_cal
	=	cal:[^@]+ {
			var c = cal.join("");
			if (c == "GREGORIAN") return null;
			if (c == "JULIAN") return null;
			if (c == "HEBREW") return null;
			if (c == "FRENCH R") return null;
			return c;
		}

date_greg
	=	d:day S m:month_engl S y:year_greg { return { day:d, month:m, year:y}; }
	/	        m:month_engl S y:year_greg { return { day:0, month:m, year:y}; }
	/	                       y:year_greg { return { day:0, month:0, year:y}; }

date_hebr
	=	d:day S m:month_hebr S y:year
	/	        m:month_hebr S y:year
	/	                       y:year

date_fren
	=	d:day S m:month_fren S y:year
	/	        m:month_fren S y:year
	/	                       y:year

year_greg
	=	y:ryear S e:epoch { return y*e; }
	/	y:ryear

ryear
	=	os_year:year "/" number { return os_year+1; }
	/	ns_year:year

epoch
	=	"BC"     { return -1; }
	/	"B.C."   { return -1; }
	/	"BCE"    { return -1; }
	/	"B.C.E." { return -1; }
	/	"AD"     { return  1; }
	/	"A.D."   { return  1; }
	/	"CE"     { return  1; }
	/	"C.E."   { return  1; }

year = y:number { return (y==0) ? null : y; }

day = number

number = digits:[0-9]+ { return parseInt(digits.join(""),10); }

month_engl
	=	"JAN" { return  1; }
	/	"FEB" { return  2; }
	/	"MAR" { return  3; }
	/	"APR" { return  4; }
	/	"MAY" { return  5; }
	/	"JUN" { return  6; }
	/	"JUL" { return  7; }
	/	"AUG" { return  8; }
	/	"SEP" { return  9; }
	/	"OCT" { return 10; }
	/	"NOV" { return 11; }
	/	"DEC" { return 12; }

month_fren
	=	"VEND" { return  1; }
	/	"BRUM" { return  2; }
	/	"FRIM" { return  3; }
	/	"NIVO" { return  4; }
	/	"PLUV" { return  5; }
	/	"VENT" { return  6; }
	/	"GERM" { return  7; }
	/	"FLOR" { return  8; }
	/	"PRAI" { return  9; }
	/	"MESS" { return 10; }
	/	"THER" { return 11; }
	/	"FRUC" { return 12; }
	/	"COMP" { return 13; }

month_hebr
	=	"TSH" { return  1; }
	/	"CSH" { return  2; }
	/	"KSL" { return  3; }
	/	"TVT" { return  4; }
	/	"SHV" { return  5; }
	/	"ADR" { return  6; }
	/	"ADS" { return  7; }
	/	"NSN" { return  8; }
	/	"IYR" { return  9; }
	/	"SVN" { return 10; }
	/	"TMZ" { return 11; }
	/	"AAV" { return 12; }
	/	"ELL" { return 13; }

S
	=	(" ")+
