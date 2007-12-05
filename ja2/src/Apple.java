import gui.UI;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import keyboard.Keyboard;
import keyboard.KeyboardInterface;
import keyboard.KeypressQueue;
import paddle.PaddleBtnInterface;
import paddle.PaddlesInterface;
import stdio.StandardIn;
import stdio.StandardOut;
import video.Video;
import chipset.AddressBus;
import chipset.Card;
import chipset.Clock;
import chipset.EmptySlot;
import chipset.Memory;
import chipset.Slots;
import chipset.cpu.CPU6502;
import disk.DiskBytes;
import disk.DiskDriveSimple;
import disk.DiskInterface;
import disk.DiskState;
import disk.StepperMotor;

/*
 * Created on Dec 4, 2007
 */
public class Apple
{
	private final Clock clock;
	private final CPU6502 cpu;

	public Apple(final UI ui, final BufferedImage screenImage, final KeypressQueue keypresses, final KeypressQueue stdinkeys, final PaddlesInterface paddles, final PaddleBtnInterface pdlbtns, final DiskBytes[] rdisk)
	{
    	final DiskDriveSimple drive = new DiskDriveSimple(rdisk);
    	final StepperMotor arm = new StepperMotor();
    	final DiskState diskState = new DiskState(drive,arm);
    	final DiskInterface disk = new DiskInterface(diskState,ui);

    	final List<Card> cards = Arrays.<Card>asList(new Card[]
		{
	    	/* 0 */ new EmptySlot(),
	    	/* 1 */ new StandardOut(),
	    	/* 2 */ new StandardIn(ui,stdinkeys),
	    	/* 3 */ new EmptySlot(),
	    	/* 4 */ new EmptySlot(),
	    	/* 5 */ new EmptySlot(),
	    	/* 6 */ disk,
	    	/* 7 */ new EmptySlot()
		});
    	final Slots slots = new Slots(cards);

    	final Memory memory = new Memory();
        final Video video = new Video(ui,memory,screenImage);
    	final KeyboardInterface keyboard = new Keyboard(keypresses,ui);

    	final AddressBus addressBus = new AddressBus(memory,keyboard,video,paddles,pdlbtns,slots);

    	this.cpu = new CPU6502(addressBus);

    	this.clock = new Clock(cpu,video,drive,paddles,ui);
	}

	public void start()
	{
		this.clock.run();
	}

	public void stop()
	{
		// TODO check for unsaved changes to disks before exiting application (only in GUI mode)

		// use another thread (a daemon one) to avoid any deadlocks
		// (for example, if this method is called on the dispatch thread)
		final Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				if (Apple.this.clock != null)
				{
					Apple.this.clock.shutdown();
				}
			}
		});
		th.setName("Apple-stop");
		th.setDaemon(true);
		th.start();
	}

	public void reset()
	{
		this.cpu.reset();
	}
}
