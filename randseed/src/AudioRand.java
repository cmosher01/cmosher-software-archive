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
	 */
	public static void main(String[] args)
	{
    	Mixer.Info[] rInfo = AudioSystem.getMixerInfo();
    	for (int i = 0; i < rInfo.length; i++)
		{
			Mixer.Info info = rInfo[i];
			System.out.println(info.toString());
			System.out.println(info.getDescription());

			Mixer mixer = AudioSystem.getMixer(info);
			Line[] lines = mixer.getTargetLines();
			for (int j = 0; j < lines.length; j++)
			{
				Line line = lines[j];
				Line.Info lineInfo = line.getLineInfo();
				System.out.print("line "+(j+1)+". ");
				System.out.println(lineInfo.toString());
			}

			System.out.println();
		}

    	AudioFormat audioFormat = new AudioFormat(AudioSystem.NOT_SPECIFIED,AudioSystem.NOT_SPECIFIED,AudioSystem.NOT_SPECIFIED,false,false);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class,audioFormat);
		TargetDataLine targetDataLine = null;
		try
		{
			targetDataLine = (TargetDataLine)AudioSystem.getLine(info);
			targetDataLine.open(audioFormat);
		}
		catch (LineUnavailableException e)
		{
			out("unable to get a recording line");
			e.printStackTrace();
			System.exit(1);
		}
	}
}
