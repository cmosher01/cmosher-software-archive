/*
 * Created on Apr 23, 2005
 */

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class AudioRand
{
	/**
	 * @param args
	 * @throws LineUnavailableException 
	 */
	public static void main(String[] args) throws LineUnavailableException
	{
    	AudioFormat audioFormat = new AudioFormat(44100,8,1,true,false);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class,audioFormat);
		TargetDataLine targetDataLine = null;
		targetDataLine = (TargetDataLine)AudioSystem.getLine(info);
		targetDataLine.open(audioFormat);
		targetDataLine.start();
		byte[] rb = new byte[4*8*99];
		targetDataLine.read(rb,0,rb.length);
		targetDataLine.stop();
		targetDataLine.close();

		long seed = 0;
		for (int i = 0; i < rb.length; i++)
		{
			if (i % 99 == 0)
			{
				byte b = rb[i];
				seed <<= 1;
				if ((b & 1) != 0)
				{
					seed |= 1;
				}
			}
		}
		System.out.println(Long.toHexString(seed));
	}
}
