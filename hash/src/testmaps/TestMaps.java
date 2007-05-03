/*
 * Created on May 3, 2006
 */
package testmaps;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.surveysampling.hash.SSIHashMap;

public class TestMaps
{
    public static void main(String[] args)
    {
//        System.out.println((int)(Float.POSITIVE_INFINITY * 64));
//        final SSIHashMap<MyObj,Integer> map = new SSIHashMap<MyObj,Integer>(64,Float.POSITIVE_INFINITY);
//        for (int i = 1; i <= 64; ++i)
//            putToMap(i,map);


        final SortedMap<Integer,String> set = new TreeMap<Integer,String>();
        set.put(1,"one");
        set.put(3,"three");
        set.put(5,"five");
        set.put(6,"six");
        set.put(8,"eight");

        final SortedMap<Integer,String> items2to8 = set.subMap(5,7);
        for (final Map.Entry<Integer,String> entry : items2to8.entrySet())
        {
            System.out.println(entry.getValue());
        }
    }

    private static void putToMap(final int n, final Map<MyObj, Integer> map)
    {
        map.put(new MyObj(n),n);
    }
}
