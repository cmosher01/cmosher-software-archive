import java.util.ArrayList;
import java.util.List;

/*
 * Created on Aug 31, 2004
 */

/**
 * Contains the "main" method (external entry point) for
 * the application. This class is in the default package
 * so that the program can be run with the following command:
 * <code>java Ja2 [arguments]</code>
 * 
 * @author Chris Mosher
 */
public final class Ja2
{
    /**
     * This class is never instantiated.
     */
    private Ja2()
    {
        assert false : "Cannot instantiate Ja2 class.";
    }

    /**
     * @param args
     * @throws ApplicationAborting
     */
    public static void main(String[] args)
    {
    	final Memory memory = new Memory();

    	final Video video = new Video(memory);

    	final CPU6502 cpu = new CPU6502(memory);

    	final List<Clock.Timed> rTimed = new ArrayList<Clock.Timed>();
    	rTimed.add(cpu);
    	rTimed.add(video);
    	final Clock clock = new Clock(rTimed);

    	clock.run();
    }
}
