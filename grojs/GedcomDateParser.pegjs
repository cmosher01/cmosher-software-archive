start
	= date_value

date_value
	= date / date_period / date_range / date_approx / date_int / date_phrase

date_int
	= "INT" S date S date_phrase

date_phrase
	= phrase

phrase
	= "(" phrase:[^)]* ")" { return phrase.join(""); }

date_period
	= "FROM" S f:date S "TO" S t:date
	/ "FROM" S f:date
	/ "TO" S t:date

date_range
	= "BET" S a:date S "AND" S b:date
	/ "AFT" S a:date
	/ "BEF" S b:date

date_approx
	= "ABT" S d:date
	/ "CAL" S d:date
	/ "EST" S d:date

date
	= cal:"@#DGREGORIAN@" S d:date_greg
	/ cal:"@#DJULIAN@" S d:date_greg { d.julian=true; return d; }
	/ cal:"@#DHEBREW@"  S d:date_hebr { return null; /* not yet implemented */ }
	/ cal:"@#DFRENCH R@" S d:date_fren { return null; /* not yet implemented */ }
	/ cal:"@#D" other_cal "@" S unknown:.* { return null; }
	/ d:date_greg

other_cal
	= cal:[^@]+ {
			var c = cal.join("");
			if (c == "GREGORIAN") return null;
			if (c == "JULIAN") return null;
			if (c == "HEBREW") return null;
			if (c == "FRENCH R") return null;
			return c;
		}

date_greg
	= d:day S m:month_eng S y:year_greg { return {day:d,month:m,year:y}; }
	/ m:month_eng S y:year_greg { return {day:0,month:m,year:y}; }
	/ y:year_greg { return {day:0,month:0,year:y}; }

date_hebr
	= day S month_hebr S year
	/ month_hebr S year
	/ year

date_fren
	= day S month_fren S year
	/ month_fren S year
	/ year

year_greg
	= os_year:number "/" number { return os_year+1; }
	/ number

year = number

day = number

number = digits:[0-9]+ { return parseInt(digits.join(""),10); }

month_eng
	= "JAN" { return 1; }
	/ "FEB" { return 2; }
	/ "MAR" { return 3; }
	/ "APR" { return 4; }
	/ "MAY" { return 5; }
	/ "JUN" { return 6; }
	/ "JUL" { return 7; }
	/ "AUG" { return 8; }
	/ "SEP" { return 9; }
	/ "OCT" { return 10; }
	/ "NOV" { return 11; }
	/ "DEC" { return 12; }

month_fren
	= "VEND" { return 1; }
	/ "BRUM" { return 2; }
	/ "FRIM" { return 3; }
	/ "NIVO" { return 4; }
	/ "PLUV" { return 5; }
	/ "VENT" { return 6; }
	/ "GERM" { return 7; }
	/ "FLOR" { return 8; }
	/ "PRAI" { return 9; }
	/ "MESS" { return 10; }
	/ "THER" { return 11; }
	/ "FRUC" { return 12; }
	/ "COMP" { return 13; }

month_hebr
	= "TSH" { return 1; }
	/ "CSH" { return 2; }
	/ "KSL" { return 3; }
	/ "TVT" { return 4; }
	/ "SHV" { return 5; }
	/ "ADR" { return 6; }
	/ "ADS" { return 7; }
	/ "NSN" { return 8; }
	/ "IYR" { return 9; }
	/ "SVN" { return 10; }
	/ "TMZ" { return 11; }
	/ "AAV" { return 12; }
	/ "ELL" { return 13; }

S
	= (" ")+ { return " "; }
