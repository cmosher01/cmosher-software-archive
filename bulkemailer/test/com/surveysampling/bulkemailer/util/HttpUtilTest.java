/*
 * Created on May 14, 2004
 */
package com.surveysampling.bulkemailer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class HttpUtilTest extends TestCase
{
    public HttpUtilTest(String name)
    {
        super(name);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(HttpUtilTest.class);
    }

    public void testParseQueryStringSimple()
    {
        List<String> key = new ArrayList<String>();
        List<String> val = new ArrayList<String>();

        key.clear(); val.clear();
        key.add("rate"); val.add("5000");
        key.add("timeout"); val.add("2000");
        assertByKeyValList(key,val,"rate=5000&timeout=2000");

        key.clear(); val.clear();
        key.add("a"); val.add("1");
        key.add("b"); val.add("2");
        key.add("c"); val.add("3");
        assertByKeyValList(key,val,"a=99&a=1&b=2&c=3");

        key.clear(); val.clear();
        key.add("test"); val.add("testing");
        assertByKeyValList(key,val,"test=testing");

        key.clear(); val.clear();
        assertByKeyValList(key,val,"");
    }

    private void assertByKeyValList(List rkey, List rval, String string)
    {
        Map<String,String> mapParamToValue = new HashMap<String,String>();
        HttpUtil.parseQueryStringSimple(string, mapParamToValue);
        Iterator ikey = rkey.iterator();
        Iterator ival = rval.iterator();
        while (ikey.hasNext())
        {
            String key = (String)ikey.next();
            String val = (String)ival.next();
            assertTrue(mapParamToValue.containsKey(key));
            assertEquals(val,mapParamToValue.get(key));
        }
        assertEquals(rkey.size(),mapParamToValue.size());
    }
}
