/*
 * Created on Mar 14, 2006
 */
package com.surveysampling.hash.algorithm;

import java.util.Collection;

public class HashManager
{
    public static void addAllHashingAlgorithms(final Collection<HashingAlgorithm> addTo)
    {
        addTo.add(new APHash());
        addTo.add(new BKDRHash());
        addTo.add(new DEKHash());
        addTo.add(new DJBHash());
        addTo.add(new JSHash());
        addTo.add(new PJWHash());
        addTo.add(new RSHash());
        addTo.add(new SDBMHash());
    }
}
