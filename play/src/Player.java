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

    public static void main(String[] args)
    {
    	LoggingInitializer.init();
    	Player p = new Player();
    	p.play();
    }

    /**
     * 
     */
    private void play()
    {
    	log.finest("This is a message displayed at the finest level of detail.");
    }
}
