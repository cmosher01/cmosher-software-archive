package com.surveysampling.tools;

import java.io.File;
import java.io.IOException;

/**
 * Provides static functions relating to file handling.
 * @author Chris Mosher
 */
public class Files
{
    private Files()
    {
    }

    public static File relativePath(File from, File to) throws IOException
    {
        from = from.getCanonicalFile();
        to = to.getCanonicalFile();

        String sFrom = from.getPath();
        String sTo = to.getPath();

        int dif = -1;
        for (int i = 0; i < Math.min(sFrom.length(),sTo.length()) && dif < 0; ++i)
        {
            char cf = sFrom.charAt(i);
            char ct = sTo.charAt(i);
            if (cf != ct)
                dif = i;
        }

        StringBuffer sb = new StringBuffer(256);
        if (dif == -1)
        {
            dif = Math.min(sFrom.length(),sTo.length());
        }

        int lastdir = sTo.lastIndexOf(File.separator,dif);
        if (0 <= lastdir && lastdir < Math.min(sFrom.length(),sTo.length())-1)
        {
            dif = lastdir+1;
        }

        sFrom = sFrom.substring(dif);
        sTo = sTo.substring(dif);

        for (int i = sFrom.indexOf(File.separator); i >= 0; i = sFrom.indexOf(File.separator,i+1))
        {
            sb.append("..");
            sb.append(File.separator);
        }
        sb.append(sTo);

        if (File.separator.length() <= sb.length() && sb.subSequence(0,File.separator.length()).equals(File.separator))
            sb.delete(0,File.separator.length());

        return new File(sb.toString());
    }
}
