package emu;

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
import keyboard.KeyboardBufferMode;
import keyboard.KeyboardInterface;
import keyboard.KeypressQueue;
import chipset.AddressBus;
import chipset.Memory;
import chipset.PowerUpReset;
import chipset.Slots;
import chipset.Timable;
import chipset.cpu.CPU6502;

/*
 * Created on Jan 28, 2008
 */
public class Apple2 implements Timable
{
	final Memory ram;
	final Memory rom;
	final Slots slots;
	private final VideoMode videoMode;
	private final KeyboardInterface keyboard;
	private final PaddlesInterface paddles;
	private final SpeakerClicker speaker;
	private final AddressBus addressBus;
	private final PictureGenerator picgen;
	private final TextCharacters textRows;
	private final Video video;
	private final CPU6502 cpu;
	private final PowerUpReset powerUpReset;

	public Apple2(final KeypressQueue keypresses, final PaddleButtonStates paddleButtonStates, final VideoDisplayDevice tv, final HyperMode hyper, final KeyboardBufferMode buffered) throws IOException
	{
		this.slots = new Slots();
		this.videoMode = new VideoMode();
		this.keyboard = new Keyboard(keypresses,hyper,buffered);
		this.paddles = new Paddles();
		this.speaker = new SpeakerClicker();
		this.ram = new Memory(AddressBus.MOTHERBOARD_RAM_SIZ);
		this.rom = new Memory(AddressBus.MOTHERBOARD_ROM_SIZ);
		this.addressBus = new AddressBus(this.ram,this.rom,this.keyboard,this.videoMode,this.paddles,paddleButtonStates,this.speaker,this.slots);
		this.picgen = new PictureGenerator(this.videoMode,tv);
		this.textRows = new TextCharacters();
		this.video = new Video(this.videoMode,this.addressBus,this.picgen,this.textRows);
		this.cpu = new CPU6502(this.addressBus);
		this.powerUpReset = new PowerUpReset(this);
	}

	public void tick()
	{
		this.cpu.tick();
		this.video.tick();
		this.paddles.tick();
		this.speaker.tick();
		this.powerUpReset.tick();
	}

	public void powerOn()
	{
		this.ram.powerOn();
		this.cpu.powerOn();
		this.videoMode.powerOn();
		this.video.powerOn();
		this.picgen.powerOn();
		this.powerUpReset.powerOn();
		// TODO clear up all other things for Apple ][ power-on
	}

	public void powerOff()
	{
		this.ram.powerOff();
	}

	public void reset()
	{
		this.cpu.reset();
		this.slots.reset();
	}
}
