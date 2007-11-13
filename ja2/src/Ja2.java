import gui.FrameManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import chipset.CPU6502;
import chipset.Clock;
import chipset.Memory;
import keyboard.FnKeyHandler;
import keyboard.Keyboard;
import video.Video;
import disk.DiskBytes;
import disk.DiskDriveSimple;
import disk.DiskInterface;
import disk.StepperMotor;
import disk.TapeDriveMemory;
import disk.TapeInterface;

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

	Clock clock;

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



    	final DiskBytes disk1 = new DiskBytes();
    	final DiskBytes disk2 = new DiskBytes();
    	final DiskDriveSimple drive = new DiskDriveSimple(new DiskBytes[] {disk1,disk2});
    	final StepperMotor arm = new StepperMotor();
    	final DiskInterface disk = new DiskInterface(drive,arm,this.framer);



//    	final TapeDriveMemory tape = new TapeDriveMemory();
//    	final TapeInterface tapedrive = new TapeInterface(tape);



    	final Video video = new Video();

        final Memory memory = new Memory(keyboard,video,disk,null);
        video.setMemory(memory);


//        final InputStream romImage = new FileInputStream(new File("C:\\eclipse_organize\\postsvn_workspace\\apple2src\\firmware/rom/apple2p_d000.rom"));
//        memory.load(0xD000,romImage);
//        romImage.close();
//
//        final InputStream monromImage = new FileInputStream(new File("C:\\eclipse_organize\\postsvn_workspace\\apple2src\\firmware/rom/apple2p_f800.rom"));
//        memory.load(0xF800,monromImage);
//        monromImage.close();
//
//        final InputStream diskromImage = new FileInputStream(new File("C:\\eclipse_organize\\postsvn_workspace\\apple2src\\build\\firmware\\disk2_16sector\\disk2_A$C600_L$0100_16sector"));
//        memory.load(0xC600,diskromImage);
//        diskromImage.close();

//        final InputStream c200romImage = getClass().getResourceAsStream("unknown_from_2e_c200.rom");
//        memory.load(0xc200,c200romImage);
//        romImage.close();

//        final InputStream c100romImage = getClass().getResourceAsStream("apple2e_c100.rom");
//        memory.load(0xc100,c100romImage);
//        romImage.close();

    	final CPU6502 cpu = new CPU6502(memory);

    	final FnKeyHandler fn = new FnKeyHandler(cpu,disk);

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

        final List<Clock.Timed> rTimed = new ArrayList<Clock.Timed>();
    	rTimed.add(video);
    	rTimed.add(cpu);
    	//rTimed.add(tapedrive);

    	this.clock = new Clock(rTimed);

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
		final String opt = nextTok(tok);
		final String val = nextTok(tok);

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
    		final Matcher matcher;
    		if ((matcher = patIMPORT.matcher(s)).matches())
    		{
    			final int addr = Integer.decode(matcher.group(1));
    			final String mem = matcher.group(2);

    			final InputStream image = new FileInputStream(new File(mem));
    	        memory.load(addr,image);
    	        image.close();
    		}
    	}
    	cfg.close();
	}

	private JMenuBar createAppMenuBar()
	{
		final JMenuBar menubar = new JMenuBar();
        appendMenus(menubar);
        return menubar;
	}

	private void appendMenus(final JMenuBar bar)
	{
		final JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);

		appendMenuItems(menuFile);

		bar.add(menuFile);
	}

	private void appendMenuItems(final JMenu menu)
	{
		final JMenuItem itemFileExit = new JMenuItem("Exit");
		itemFileExit.setMnemonic(KeyEvent.VK_X);
		itemFileExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				close();
			}
		});
		menu.add(itemFileExit);
	}

	public void close()
	{
		this.clock.shutdown();
		this.framer.close(); // this exits the app
	}

	private static String nextTok(final StringTokenizer tok)
	{
		if (!tok.hasMoreTokens())
		{
			return "";
		}
		return tok.nextToken();
	}
}
