import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.logging.Logger;

import nu.mine.mosher.charsets.GedcomAnselCharsetProvider;
import nu.mine.mosher.core.Immutable;
import nu.mine.mosher.logging.LoggingInitializer;

/*
 * Created on Jun 7, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Player implements Immutable
{
    private Logger log = Logger.global;

    /**
     * 
     */
    public Player()
    {
        super();
    }

    public static void main(String[] args) throws Throwable
    {
        LoggingInitializer.init();

		GedcomAnselCharsetProvider cp;
		if (!Charset.isSupported("x-gedcom-ansel"))
		{
			System.err.println("You must create a file META-INF/services/java.nio.charsets.spi.CharsetProvider");
			System.err.println("in the current classpath, with one line in it:");
			System.err.println("nu.mine.mosher.charsets.GedcomAnselCharsetProvider");
			throw new UnsupportedCharsetException("x-gedcom-ansel");
		}

        InputStream stream;
        if (args.length > 0)
        {
            stream = new FileInputStream(args[0]);
        }
        else
        {
            stream = System.in;
        }

		InputStreamReader reader = new InputStreamReader(stream,"x-gedcom-ansel");
		BufferedReader bufrd = new BufferedReader(reader);

        String s = bufrd.readLine();
        while (s != null)
        {
            System.out.println(s);
            s = bufrd.readLine();
        }
		System.out.flush();
		bufrd.close();
    }

    /**
     * 
     */
    private void play()
    {
        log.finest("This is a message displayed at the finest level of detail.");
    }
}
