/*
 * Created on May 4, 2006
 */
package com.surveysampling.hash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TestAssoc
{

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException
    {
        final int neg = -45;
        System.out.println(Integer.toHexString(neg));
        System.out.println(neg % 20);
        System.out.println(Math.abs(neg % 20));
        System.out.println((neg % 20) + 20);

        final int masked = neg & Integer.MAX_VALUE;
        System.out.println(Integer.toHexString(masked));
        System.out.println(masked % 20);
////        final SimpleHashAssocArray<EnglishWord,Integer> map = new SimpleHashAssocArray<EnglishWord,Integer>(6151);
//        final Map<EnglishWord,Integer> map = new HashMap<EnglishWord,Integer>(8192,Float.POSITIVE_INFINITY);
//
//        final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("c:/dictionary/dict.txt"))));
//        for (String word = in.readLine(); word != null; word = in.readLine())
//        {
//            final EnglishWord engword = new EnglishWord(word);
//            map.put(engword,1);
//        }
//        in.close();
//
////        System.out.println("bucket sizes:");
////        dumpBuckets(map);
//        final EnglishWord testing = new EnglishWord("testing");
//        final EnglishWord some = new EnglishWord("some");
//        final EnglishWord random = new EnglishWord("random");
//        final EnglishWord lookups = new EnglishWord("lookups");
//        final EnglishWord to = new EnglishWord("to");
//        final EnglishWord see = new EnglishWord("see");
//        final EnglishWord how = new EnglishWord("how");
//        final EnglishWord fast = new EnglishWord("fast");
//        final EnglishWord they = new EnglishWord("they");
//        final EnglishWord are = new EnglishWord("are");
//        final long start = System.currentTimeMillis();
//        map.get(testing);
//        map.get(some);
//        map.get(random);
//        map.get(lookups);
//        map.get(to);
//        map.get(see);
//        map.get(how);
//        map.get(fast);
//        map.get(they);
//        map.get(are);
//        final long end = System.currentTimeMillis();
//        System.out.println(end-start);
    }

    private static void dumpBuckets(final SimpleHashAssocArray<?,?> map)
    {
        final Collection<Integer> rSize = new ArrayList<Integer>(11);
        map.getBucketSizes(rSize);
        for (final Integer size : rSize)
        {
            System.out.println(size);
        }
    }

}
