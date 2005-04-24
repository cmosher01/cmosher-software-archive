/*
 * Created on Apr 23, 2005
 */

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

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
			Line[] lines = mixer.getSourceLines();
			for (int j = 0; j < lines.length; j++)
			{
				Line line = lines[j];
				Line.Info lineInfo = line.getLineInfo();
				System.out.print("line "+(j+1)+". ");
				System.out.println(lineInfo.toString());
			}

			System.out.println();
		}
	}
}
