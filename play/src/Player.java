import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.logging.Logger;

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
        BufferedReader in;

		Charset cs = Charset.forName("x-gedcom-ansel");
		System.out.println(cs.displayName());

        if (args.length > 0)
        {
            // open the named file
            in = new BufferedReader(new FileReader(args[0]));
        }
        else
        {
            // wrap a BufferedReader around stdin
            in = new BufferedReader(new InputStreamReader(System.in));
        }

        PrintStream out = new PrintStream(System.out,false,"x-gedcom-ansel");

        String s = in.readLine();
        while (s != null)
        {
            out.println(s);
			s = in.readLine();
        }

        out.flush();
        out.close();
    }

    /**
     * 
     */
    private void play()
    {
    	log.finest("This is a message displayed at the finest level of detail.");
    }
}
