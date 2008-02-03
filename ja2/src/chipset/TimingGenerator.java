package chipset;

import java.util.concurrent.atomic.AtomicBoolean;
import paddle.PaddlesInterface;
import chipset.cpu.CPU6502;
import speaker.SpeakerClicker;
import util.Util;
import video.Video;



/*
 * Created on Aug 1, 2007
 */
public class TimingGenerator extends TimingGeneratorAbstract
{
	private final CPU6502 cpu;
	private final Video video;
	private final PaddlesInterface paddles;
	private final SpeakerClicker speaker;



	public TimingGenerator(final CPU6502 cpu, final Video video, final PaddlesInterface paddles, final SpeakerClicker speaker, final Throttle throttle)
	{
		super(throttle);
		this.cpu = cpu;
		this.video = video;
		this.paddles = paddles;
		this.speaker = speaker;
	}

	@Override
	protected void tick()
	{
		this.cpu.tick();
		this.video.tick();
		this.paddles.tick();
		this.speaker.tick();
	}
}
