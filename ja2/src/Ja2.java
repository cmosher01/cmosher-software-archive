import gui.FrameManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import keyboard.ClipboardHandler;
import keyboard.FnKeyHandler;
import keyboard.Keyboard;
import util.Util;
import video.Video;
import chipset.CPU6502;
import chipset.Clock;
import chipset.Memory;
import disk.DiskBytes;
import disk.DiskDriveSimple;
import disk.DiskInterface;
import disk.StepperMotor;

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
	private final FrameManager framer = new FrameManager();

	private Clock clock;

	private String config = "ja2.cfg";

	private Ja2()
    {
    	// only instantiated by main
    }

	/**
     * @param args
     * @throws IOException 
     * @throws ApplicationAborting
     */
    public static void main(final String... args) throws IOException, InterruptedException, InvocationTargetException
    {
    	SwingUtilities.invokeAndWait(new Runnable()
    	{
			public void run()
			{
		    	final Ja2 program = new Ja2();
		    	program.run(args);
			}
    	}
    	);
    }

    public void run(final String... args)
	{
    	try
		{
			tryRun(args);
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

    private void tryRun(final String... args) throws IOException
    {
    	parseArgs();

    	final Keyboard keyboard = new Keyboard();
    	final ClipboardHandler clip = new ClipboardHandler(keyboard);



    	final DiskBytes disk1 = new DiskBytes();
    	final DiskBytes disk2 = new DiskBytes();
    	final DiskDriveSimple drive = new DiskDriveSimple(new DiskBytes[] {disk1,disk2});
    	final StepperMotor arm = new StepperMotor();
    	final DiskInterface disk = new DiskInterface(drive,arm,this.framer);



    	final Video video = new Video();

        final Memory memory = new Memory(keyboard,video,disk);
        video.setMemory(memory);


    	final CPU6502 cpu = new CPU6502(memory);

    	final FnKeyHandler fn = new FnKeyHandler(cpu,disk,clip,video);

        // create the main frame window for the application
        this.framer.init(
	    	new WindowAdapter()
	    	{
				@Override
				public void windowClosing(final WindowEvent e)
				{
					close();
				}
	    	},
	    	video,disk1,disk2);

        video.addKeyListener(keyboard);
        video.addKeyListener(fn);
		video.setFocusTraversalKeysEnabled(false);
		video.requestFocus();

    	this.clock = new Clock(cpu,video,drive);

        parseConfig(memory);

        this.clock.run();
    	disk.updatePanel();
	}

    private void parseArgs(final String... args)
	{
		for (final String arg : args)
		{
			if (arg.startsWith("--"))
			{
				parseArg(arg.substring(2));
			}
			else
			{
				throw new IllegalArgumentException(arg);
			}
		}
	}

	private void parseArg(final String arg)
	{
		final StringTokenizer tok = new StringTokenizer(arg,"=");
		final String opt = Util.nextTok(tok);
		final String val = Util.nextTok(tok);

		if (opt.equals("config"))
		{
			this.config = val;
		}
		else
		{
			throw new IllegalArgumentException(arg);
		}
	}

	private static final Pattern patIMPORT = Pattern.compile("import\\s+(.+)\\s+(.+)");
	private void parseConfig(Memory memory) throws IOException
	{
    	final BufferedReader cfg = new BufferedReader(new InputStreamReader(new FileInputStream(this.config)));
    	for (String s = cfg.readLine(); s != null; s = cfg.readLine())
    	{
    		int comment = s.indexOf('#');
    		if (comment >= 0)
    		{
    			s = s.substring(0,comment);
    		}
    		s = s.trim();
    		if (s.isEmpty())
    		{
    			continue;
    		}

    		final Matcher matcher;
    		if ((matcher = patIMPORT.matcher(s)).matches())
    		{
    			final int addr = Integer.decode(matcher.group(1));
    			final String mem = matcher.group(2);

    			final InputStream image = new FileInputStream(new File(mem));
    	        memory.load(addr,image);
    	        image.close();
    		}
    		else
    		{
    			throw new IllegalArgumentException("Error in config file: "+s);
    		}
    	}
    	cfg.close();
	}

	public void close()
	{
		if (this.clock != null)
		{
			this.clock.shutdown();
		}
		this.framer.close(); // this exits the app
	}
}
