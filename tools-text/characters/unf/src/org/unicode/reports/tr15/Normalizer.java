package org.unicode.reports.tr15;

/**
 * Implements Unicode Normalization Forms C, D, KC, KD.<br>
 * See UTR#15 for details.<br>
 * Copyright � 1998-1999 Unicode, Inc. All Rights Reserved.<br>
 * The Unicode Consortium makes no expressed or implied warranty of any
 * kind, and assumes no liability for errors or omissions.
 * No liability is assumed for incidental and consequential damages
 * in connection with or arising out of the use of the information here.
 * @author Mark Davis
 */

public class Normalizer
{
    static final String copyright = "Copyright � 1998-1999 Unicode, Inc.";

	public Normalizer()
	{
		this(C,true);
	}

    /**
     * Create a normalizer for a given form.
     */
    public Normalizer(byte form, boolean fullData)
    {
        this.form = form;
        if (data == null)
            data = NormalizerBuilder.build(fullData); // load 1st time
    }

    /**
    * Masks for the form selector
    */
    static final byte COMPATIBILITY_MASK = 1, COMPOSITION_MASK = 2;

    /**
    * Normalization Form Selector
    */
    public static final byte D = 0, C = COMPOSITION_MASK, KD = COMPATIBILITY_MASK, KC = (byte) (COMPATIBILITY_MASK + COMPOSITION_MASK);

    /**
    * Normalizes text according to the chosen form, 
    * replacing contents of the target buffer.
    * @param   source      the original text, unnormalized
    * @param   target      the resulting normalized text
    */
    public StringBuffer normalize(String source, StringBuffer target)
    {

        // First decompose the source into target,
        // then compose if the form requires.

        if (source.length() != 0)
        {
            internalDecompose(source, target);
            if ((form & COMPOSITION_MASK) != 0)
            {
                internalCompose(target);
            }
        }
        return target;
    }

    /**
    * Normalizes text according to the chosen form
    * @param   source      the original text, unnormalized
    * @return  target      the resulting normalized text
    */
    public String normalize(String source)
    {
        return normalize(source, new StringBuffer()).toString();
    }

    // ======================================
    //                  PRIVATES
    // ======================================

    /**
     * The current form.
     */
    private byte form;

    /**
    * Decomposes text, either canonical or compatibility,
    * replacing contents of the target buffer.
    * @param   form        the normalization form. If COMPATIBILITY_MASK
    *                      bit is on in this byte, then selects the recursive 
    *                      compatibility decomposition, otherwise selects
    *                      the recursive canonical decomposition.
    * @param   source      the original text, unnormalized
    * @param   target      the resulting normalized text
    */
    private void internalDecompose(String source, StringBuffer target)
    {
        StringBuffer buffer = new StringBuffer();
        boolean canonical = (form & COMPATIBILITY_MASK) == 0;
        for (int i = 0; i < source.length(); ++i)
        {
            buffer.setLength(0);
            data.getRecursiveDecomposition(canonical, source.charAt(i), buffer);

            // add all of the characters in the decomposition.
            // (may be just the original character, if there was
            // no decomposition mapping)

            for (int j = 0; j < buffer.length(); ++j)
            {
                char ch = buffer.charAt(j);
                int chClass = data.getCanonicalClass(ch);
                int k = target.length(); // insertion point
                if (chClass != 0)
                {

                    // bubble-sort combining marks as necessary

                    for (; k > 0; --k)
                    {
                        if (data.getCanonicalClass(target.charAt(k - 1)) <= chClass)
                            break;
                    }
                }
                target.insert(k, ch);
            }
        }
    }

    /**
    * Composes text in place. Target must already
    * have been decomposed.
    * @param   target      input: decomposed text.
    *                      output: the resulting normalized text.
    */
    private void internalCompose(StringBuffer target)
    {

        int starterPos = 0, compPos = 1;
        char starterCh = target.charAt(0);
        int lastClass = data.getCanonicalClass(starterCh);
        if (lastClass != 0)
            lastClass = 256; // fix for irregular combining sequence

        // Loop on the decomposed characters, combining where possible

        for (int decompPos = 1; decompPos < target.length(); ++decompPos)
        {
            char ch = target.charAt(decompPos);
            int chClass = data.getCanonicalClass(ch);
            char composite = data.getPairwiseComposition(starterCh, ch);
            if (composite != NormalizerData.NOT_COMPOSITE && (lastClass < chClass || lastClass == 0))
            {
                target.setCharAt(starterPos, composite);
                starterCh = composite;
            }
            else
            {
                if (chClass == 0)
                {
                    starterPos = compPos;
                    starterCh = ch;
                }
                lastClass = chClass;
                target.setCharAt(compPos++, ch);
            }
        }
        target.setLength(compPos);
    }

    /**
    * Contains normalization data from the Unicode Character Database.
    * use false for the minimal set, true for the real set.  
    */
    private static NormalizerData data = null;

    /**
    * Just accessible for testing.
    */
    boolean getExcluded(char ch)
    {
        return data.getExcluded(ch);
    }

    /**
    * Just accessible for testing.
    */
    String getRawDecompositionMapping(char ch)
    {
        return data.getRawDecompositionMapping(ch);
    }
}
