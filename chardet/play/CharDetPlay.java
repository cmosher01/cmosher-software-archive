import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;

/*
 * TODO
 *
 * Created on May 24, 2004
 */

/**
 * TODO
 */
public class CharDetPlay
{

    public static void main(String[] args) throws IOException
    {
        if (args.length < 1)
        {
            throw new IllegalArgumentException("usage: CharDetPlay filespec");
        }

        nsDetector det = new nsDetector(nsPSMDetector.ALL);

        det.Init(new nsICharsetDetectionObserver()
        {
            public void Notify(String charset)
            {
                System.out.println("---------------- FOUND: "+charset);
            }
        });

        BufferedInputStream imp = new BufferedInputStream(new FileInputStream(new File(args[0])));

        byte[] buf = new byte[8*1024];
        int len;
        boolean done = false;
        boolean isAscii = true;

        while ((len = imp.read(buf, 0, buf.length)) != -1)
        {

            // Check if the stream is only ascii.
            if (isAscii)
                isAscii = det.isAscii(buf, len);

            // DoIt if non-ascii and not done yet.
            if (!isAscii && !done)
                done = det.DoIt(buf, len, false);
        }
        det.DataEnd();

        if (isAscii)
        {
            System.out.println("---------charset is ASCII");
        }

        String prob[] = det.getProbableCharsets();
        for (int i = 0; i < prob.length; i++)
        {
            System.out.println("---------FOUND probable charset: " + prob[i]);
        }
    }
}
