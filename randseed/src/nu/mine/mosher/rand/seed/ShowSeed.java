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
	private final RandSeedListener rand;
	public ShowSeed(final RandSeedListener rand)
	{
		this.rand = rand;
	}
	/**
	 * @param e
	 */
	public void actionPerformed(ActionEvent e)
	{
		this.rand.getSeed();
	}
}
