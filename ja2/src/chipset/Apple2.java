package chipset;
import java.io.IOException;
import paddle.PaddleButtonStates;
import paddle.Paddles;
import paddle.PaddlesInterface;
import speaker.SpeakerClicker;
import video.PictureGenerator;
import video.TextCharacters;
import video.Video;
import video.VideoDisplayDevice;
import video.VideoMode;
import keyboard.HyperMode;
import keyboard.Keyboard;
import keyboard.KeyboardInterface;
import keyboard.KeypressQueue;
import chipset.cpu.CPU6502;

/*
 * Created on Jan 28, 2008
 */
public class Apple2 implements Timable
{
	private static final int MOTHERBOARD_RAM_SIZE = 0xC000;
	private static final int MOTHERBOARD_ROM_SIZE = 0x10000-0xD000;

	private final Memory rom;
	private final Slots slots;
	private final HyperMode hyper;
	private final VideoMode videoMode;
	private final KeyboardInterface keyboard;
	private final PaddlesInterface paddles;
	private final PaddleButtonStates paddleButtonStates;
	private final SpeakerClicker speaker;
	private final Memory ram;
	private final AddressBus addressBus;
	private final PictureGenerator picgen;
	private final TextCharacters textRows;
	private final Video video;
	private final CPU6502 cpu;

	public Apple2(final KeypressQueue keypresses) throws IOException
	{
		this.slots = new Slots();
		this.hyper = new HyperMode();
		this.videoMode = new VideoMode();
		this.keyboard = new Keyboard(keypresses,this.hyper);
		this.paddles = new Paddles();
		this.paddleButtonStates = new PaddleButtonStates();
		this.speaker = new SpeakerClicker();
		this.ram = new Memory(MOTHERBOARD_RAM_SIZE);
		this.rom = new Memory(MOTHERBOARD_ROM_SIZE);
		this.addressBus = new AddressBus(this.ram,this.rom,this.keyboard,this.videoMode,this.paddles,this.paddleButtonStates,this.speaker,this.slots);
		this.picgen = new PictureGenerator(this.videoMode);
		this.textRows = new TextCharacters();
		this.video = new Video(this.videoMode,this.addressBus,this.picgen,this.textRows);
		this.cpu = new CPU6502(this.addressBus);

		final RAMInitializer initRam = new RAMInitializer(this.ram);
		initRam.init();
	}

	public void tick()
	{
		this.cpu.tick();
		this.video.tick();
		this.paddles.tick();
		this.speaker.tick();
	}

	public void setDisplay(final VideoDisplayDevice display)
	{
		this.picgen.setDisplay(display);
	}
}
