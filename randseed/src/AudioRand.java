import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer.Info;

/*
 * Created on Apr 23, 2005
 */
/**
 * TODO
 *
 * @author Chris Mosher
 */
public class AudioRand
{
	public static void main(String[] args)
	{
    	Info[] rInfo = AudioSystem.getMixerInfo();
    	for (int i = 0; i < rInfo.length; i++)
		{
			Info info = rInfo[i];
			System.out.println(info.toString());
			System.out.println(info.getName());
			System.out.println(info.getDescription());
			System.out.println();
		}
	}
}
