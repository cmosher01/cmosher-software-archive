/*
 * Created on Nov 16, 2005
 */
package nu.mine.mosher.grodb2.util;

import com.sleepycat.je.TransactionConfig;

public class TransactionConfigFactory
{
	public static TransactionConfig createReadCommitted()
	{
		final TransactionConfig config = new TransactionConfig();

		config.setReadCommitted(true);

		return config;
	}
}
