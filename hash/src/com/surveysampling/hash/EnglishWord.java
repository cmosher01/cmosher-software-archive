/*
 * Created on May 4, 2006
 */
package com.surveysampling.hash;

import java.io.UnsupportedEncodingException;

import com.surveysampling.hash.algorithm.HashingAlgorithm;
import com.surveysampling.hash.algorithm.RSHash;

public class EnglishWord
{
    private final String word;
    private final int hash;
    public EnglishWord(final String word)
    {
        this.word = word;
        this.hash = getHash();
    }
    public boolean equals(final Object object)
    {
        if (!(object instanceof EnglishWord))
        {
            return false;
        }
        final EnglishWord that = (EnglishWord)object;
        return this.word.equals(that.word);
    }
    public int hashCode()
    {
        return this.hash;
    }
    private int getHash()
    {
        final HashingAlgorithm hasher = new RSHash();
        try
        {
            return hasher.hash(this.word.getBytes("US-ASCII"));
        }
        catch (final UnsupportedEncodingException e)
        {
            throw new IllegalStateException(e);
        }
    }
    public String getWord()
    {
        return this.word;
    }
    public String toString()
    {
        return this.word;
    }
}
