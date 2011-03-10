start
  = date_int
  / date_phrase
  / date_period
  / date_range
  / date_approx
  / date

date_int
  = "INT" S ymd:date S date_phrase { return ymd; }

date_phrase
  = "(" str:[^)]* ")" { return str.join(""); }

date_period
  = "FROM" S f:date S "TO" S t:date { return { from:f, to:t }; }
  / "FROM" S f:date                 { return { from:f       }; }
  /                   "TO" S t:date { return {         to:t }; }

date_range
  = "BET" S a:date S "AND" S b:date { return { after:a, before:b }; }
  / "AFT" S a:date                  { return { after:a           }; }
  /                  "BEF" S b:date { return {          before:b }; }

date_approx
  = "ABT" S ymd:date { ymd.approx=true; return ymd; }
  / "CAL" S ymd:date { ymd.approx=true; return ymd; }
  / "EST" S ymd:date { ymd.approx=true; return ymd; }

date
  = "@#D" cal:"GREGORIAN" "@" OS ymd:date_gregor { return ymd; }
  / "@#D" cal:"JULIAN"    "@" OS ymd:date_julian { ymd.julian = true; return ymd; }
  / "@#D" cal:"HEBREW"    "@" OS ymd:date_hebrew { return null; /* not yet implemented */ }
  / "@#D" cal:"FRENCH R"  "@" OS ymd:date_french { return null; /* not yet implemented */ }
  / "@#D" cal:other_cal   "@" OS str:.*          { return cal+": "+str.join(""); }
  /                              ymd:date_julian { ymd.julian = true; return ymd; }
  /                              ymd:date_gregor { return ymd; }

other_cal
  = (! known_cal cal:[^@]+) { return cal.join(""); }

known_cal
  = "GREGORIAN" / "JULIAN" / "HEBREW" / "FRENCH R"

date_gregor
  = ymd:date_gregor_raw S e:epoch { ymd.year *= e; return ymd; }
  / ymd:date_gregor_raw

date_julian
  = ymd:date_julian_raw S e:epoch { ymd.year *= e; return ymd; }
  / ymd:date_julian_raw

date_hebrew
  = d:day S m:month_hebr S y:year
  /         m:month_hebr S y:year
  /                        y:year

date_french
  = d:day S m:month_fren S y:year
  /         m:month_fren S y:year
  /                        y:year

date_gregor_raw
  = d:day S m:month_engl S y:year { return { day:d, month:m, year:y }; }
  /         m:month_engl S y:year { return { day:0, month:m, year:y }; }
  /                        y:year { return { day:0, month:0, year:y }; }

date_julian_raw
  = d:day S m:month_engl S y:year_julian { return { day:d, month:m, year:y, julian:true }; }
  /         m:month_engl S y:year_julian { return { day:0, month:m, year:y, julian:true }; }
  /                        y:year_julian { return { day:0, month:0, year:y, julian:true }; }

year_julian
  = y:year "/" number { return y+1; }
  / y:year

year = y:number { return (y==0) ? null : y; }

day = number

number = digits:[0-9]+ { return parseInt(digits.join(""),10); }

S
  = (" ")+

OS
  = (" ")*

epoch
  = ("BC" / "B.C." / "BCE" / "B.C.E.") { return -1; }
  / ("AD" / "A.D." /  "CE" /   "C.E.") { return  1; }

month_engl
  = "JAN" { return  1; }
  / "FEB" { return  2; }
  / "MAR" { return  3; }
  / "APR" { return  4; }
  / "MAY" { return  5; }
  / "JUN" { return  6; }
  / "JUL" { return  7; }
  / "AUG" { return  8; }
  / "SEP" { return  9; }
  / "OCT" { return 10; }
  / "NOV" { return 11; }
  / "DEC" { return 12; }

month_fren
  = "VEND" { return  1; }
  / "BRUM" { return  2; }
  / "FRIM" { return  3; }
  / "NIVO" { return  4; }
  / "PLUV" { return  5; }
  / "VENT" { return  6; }
  / "GERM" { return  7; }
  / "FLOR" { return  8; }
  / "PRAI" { return  9; }
  / "MESS" { return 10; }
  / "THER" { return 11; }
  / "FRUC" { return 12; }
  / "COMP" { return 13; }

month_hebr
  = "TSH" { return  1; }
  / "CSH" { return  2; }
  / "KSL" { return  3; }
  / "TVT" { return  4; }
  / "SHV" { return  5; }
  / "ADR" { return  6; }
  / "ADS" { return  7; }
  / "NSN" { return  8; }
  / "IYR" { return  9; }
  / "SVN" { return 10; }
  / "TMZ" { return 11; }
  / "AAV" { return 12; }
  / "ELL" { return 13; }
