import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class OscillatorPlayer
{
	private static final int SAMPLE_RATE_HZ = 44100;
	private static final int BUFFER_SIZE = SAMPLE_RATE_HZ;

	private final int freq;
	private final int freqRef;
	private final AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, SAMPLE_RATE_HZ, 16, 2, 4, SAMPLE_RATE_HZ, false);
	private final Thread thread = new Thread(new Runnable()
	{
		@Override
		public void run()
		{
			try
			{
				OscillatorPlayer.this.run();
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
			}
		}
		
	});

	private boolean cancel;
	private SourceDataLine line;

	public OscillatorPlayer(final int freq, final int freqRef)
	{
		this.freq = freq;
		this.freqRef = freqRef;
		this.thread.setDaemon(true);
		this.thread.start();
	}

	private void run() throws IOException, LineUnavailableException
	{
		final AudioInputStream oscillator = createOscillator(this.freq);
		final AudioInputStream oscillatorRef = createOscillator(this.freqRef);

		final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		this.line = (SourceDataLine) AudioSystem.getLine(info);
		line.open(audioFormat);
		line.start();
		

		final byte[] abData = new byte[BUFFER_SIZE];
		while (!isCancelled())
		{
			if (line.isOpen())
			{
				final int nRead = oscillator.read(abData);
				cleanEnd(abData);
				cleanBeginning(abData);
				line.write(abData, 0, nRead);
			}

			if (line.isOpen())
			{
				final int nReadRef = oscillatorRef.read(abData);
				cleanEnd(abData);
				cleanBeginning(abData);
				line.write(abData, 0, nReadRef);
			}
		}
	}

	private static void cleanBeginning(final byte[] abData)
	{
		int i = 0;
		while (abData[1] != 0 && i < abData.length)
		{
			abData[i] = 0;
			++i;
		}
	}

	private static void cleanEnd(final byte[] abData)
	{
		int i = abData.length-1;
		while (abData[i] != 0 && i >= 0)
		{
			abData[i] = 0;
			--i;
		}
	}

	private AudioInputStream createOscillator(final int freqOsc)
	{
		return new Oscillator(freqOsc, 1F/8F, audioFormat, AudioSystem.NOT_SPECIFIED);
	}

	public synchronized void cancel()
	{
		this.cancel = true;
		if (this.line != null)
		{
			this.line.stop();
		}
	}

	private synchronized boolean isCancelled()
	{
		return this.cancel;
	}
}
