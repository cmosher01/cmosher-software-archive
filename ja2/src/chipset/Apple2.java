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
	final Memory rom;
	final Slots slots;
	final HyperMode hyper;
	final VideoMode videoMode;
	final KeyboardInterface keyboard;
	final PaddlesInterface paddles;
	final PaddleButtonStates paddleButtonStates;
	final SpeakerClicker speaker;
	final Memory ram;
	final AddressBus addressBus;
	final PictureGenerator picgen;
	final TextCharacters textRows;
	final Video video;
	final CPU6502 cpu;

	public Apple2(final KeypressQueue keypresses, final VideoDisplayDevice tv) throws IOException
	{
		this.rom = new Memory(0x10000-0xD000);
		this.slots = new Slots();
		this.hyper = new HyperMode();
		this.videoMode = new VideoMode();
		this.keyboard = new Keyboard(keypresses,this.hyper);
		this.paddles = new Paddles();
		this.paddleButtonStates = new PaddleButtonStates();
		this.speaker = new SpeakerClicker();
		this.ram = new Memory(0xC000);
		this.addressBus = new AddressBus(this.ram,this.rom,this.keyboard,this.videoMode,this.paddles,this.paddleButtonStates,this.speaker,this.slots);
		this.picgen = new PictureGenerator(tv,this.videoMode);
		this.textRows = new TextCharacters();
		this.video = new Video(this.videoMode,this.addressBus,this.picgen,this.textRows);
		this.cpu = new CPU6502(this.addressBus);
	}

	public void tick()
	{
		this.cpu.tick();
		this.video.tick();
		this.paddles.tick();
		this.speaker.tick();
	}
}
