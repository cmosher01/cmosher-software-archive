/* Generated By:JJTree&JavaCC: Do not edit this line. GedcomDateValueParserConstants.java */
package nu.mine.mosher.gedcom.date.parser;

public interface GedcomDateValueParserConstants {

  int EOF = 0;
  int FROM = 5;
  int TO = 6;
  int BEFORE = 7;
  int AFTER = 8;
  int BETWEEN = 9;
  int AND = 10;
  int ABOUT = 11;
  int CALCULATED = 12;
  int ESTIMATED = 13;
  int INTERPRETED = 14;
  int MONTH = 15;
  int CALENDAR = 16;
  int LPAREN = 17;
  int RPAREN = 18;
  int NUMBER = 19;
  int SLASH = 20;
  int AT = 21;
  int RAWTEXT = 22;
  int BC = 23;
  int CALENDAR_ESC = 24;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"FROM\"",
    "\"TO\"",
    "\"BEF\"",
    "\"AFT\"",
    "\"BET\"",
    "\"AND\"",
    "\"ABT\"",
    "\"CAL\"",
    "\"EST\"",
    "\"INT\"",
    "<MONTH>",
    "<CALENDAR>",
    "\"(\"",
    "\")\"",
    "<NUMBER>",
    "\"/\"",
    "\"@\"",
    "<RAWTEXT>",
    "<BC>",
    "\"#D\"",
  };

}