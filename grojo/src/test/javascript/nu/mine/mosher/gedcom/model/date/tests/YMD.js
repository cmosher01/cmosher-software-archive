$(function() {

  module("YMD");

  test("nominal", function() {
	  var ymd;
	  ymd = new YMD(1966,7,3);
	  equal(ymd.getYear(),1966);
	  equal(ymd.getMonth(),7);
	  equal(ymd.getDay(),3);
	  ok(ymd.isExact());
  });

  test("unknown day", function() {
	  var ymd;
	  ymd = new YMD(1966,7);
	  equal(ymd.getYear(),1966);
	  equal(ymd.getMonth(),7);
	  equal(ymd.getDay(),0);
	  ok(!ymd.isExact());
  });

  test("nominal toString", function() {
	  var ymd;
	  ymd = new YMD(1966,7,3);
	  equal(""+ymd,"1966-07-03");
  });

  test("nominal as Date", function() {
	  var ymd, d;
	  ymd = new YMD(1966,7,3);
	  d = ymd.getExactDate();
	  equal(d.toUTCString(),"Sun, 03 Jul 1966 00:00:00 GMT");
  });
});
