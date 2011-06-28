/*
 * Created on Apr 23, 2005
 */
package nu.mine.mosher.rand.seed;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class ShowSeed implements ActionListener
{
	private final RandomSeedKeyListener rand;
	public ShowSeed(final RandomSeedKeyListener rand)
	{
		this.rand = rand;
	}
	/**
	 * @param e
	 */
	public void actionPerformed(ActionEvent e)
	{
		this.rand.check();
		long seed = this.rand.getSeed();
		System.out.println(Long.toHexString(seed));
	}
}
