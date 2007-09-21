import gui.FrameManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
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
public final class Ja2 implements Runnable
{
	private final FrameManager framer = new FrameManager();

	Clock clock;

	private Ja2()
    {
    	// only instantiated by main
    }

	/**
     * @param args
     * @throws IOException 
     * @throws ApplicationAborting
     */
    public static void main(String[] args) throws IOException, InterruptedException, InvocationTargetException
    {
    	SwingUtilities.invokeAndWait(new Runnable()
    	{
			public void run()
			{
		    	final Runnable program = new Ja2();
		    	program.run();
			}
    	}
    	);
    }

    public void run()
	{
    	try
		{
			tryRun();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

    private void tryRun() throws IOException
    {
    	DiskBytes disk1 = new DiskBytes();
//    	disk1.load(new File("C:\\apple2\\research\\dos\\3.1\\hyperdos_restored.nib"));
//    	disk1.load(new File("src\\DOS33_SystemMaster_1986_patched_nodelay.nib"));
//    	disk1.load(new File("C:\\apple2\\research\\dos\\3.1\\copy2plus40.nib"));
//    	disk1.load(new File("C:\\apple2\\research\\dos\\3.1\\bloxap.nib"));
    	DiskBytes disk2 = new DiskBytes();
    	final DiskDriveSimple drive = new DiskDriveSimple(new DiskBytes[] {disk1,disk2});
    	final StepperMotor arm = new StepperMotor();
    	final DiskInterface disk = new DiskInterface(drive,arm,this.framer);

    	final Keyboard keyboard = new Keyboard();

        final Memory memory = new Memory(keyboard,disk);

//        final InputStream romImage = new FileInputStream(new File("c:/apple2/rom_images/200apple2.rom")); // D000
//        final InputStream romImage = new FileInputStream(new File("c:/apple2/rom_images/210apple2plus.rom")); // B000
        final InputStream romImage = getClass().getResourceAsStream("apple2_d000.rom");
        memory.load(0xD000,romImage);
        romImage.close();

        final InputStream diskromImage = getClass().getResourceAsStream("disk2_DOS33_c600_patched_nodelay.rom");
        memory.load(0xC600,diskromImage);
        diskromImage.close();

//        final InputStream c200romImage = getClass().getResourceAsStream("unknown_from_2e_c200.rom");
//        memory.load(0xc200,c200romImage);
//        romImage.close();

//        final InputStream c100romImage = getClass().getResourceAsStream("apple2e_c100.rom");
//        memory.load(0xc100,c100romImage);
//        romImage.close();

        final Video video = new Video(memory);

    	final CPU6502 cpu = new CPU6502(memory);

    	final FnKeyHandler fn = new FnKeyHandler(cpu);

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
    	//rTimed.add(drive);
    	rTimed.add(cpu);
    	this.clock = new Clock(rTimed);
    	this.clock.run();
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
}
