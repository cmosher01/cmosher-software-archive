/**
 * Contains methods to get a list of current threads.
 * 
 * @author Chris Mosher
 */
public class ThreadDump
{
	/**
	 * Gets a list of currently running threads in
	 * the system.
	 */
	public static Thread[] findAllThreads()
	{
		ThreadGroup group = Thread.currentThread().getThreadGroup();

		ThreadGroup topGroup = group;

		// traverse the ThreadGroup tree to the top
		while (group != null)
		{
			topGroup = group;
			group = group.getParent();
		}

		// Create a destination array that is about
		// twice as big as needed to be very confident
		// that none are clipped.
		int estimatedSize = 2*topGroup.activeCount();
		Thread[] slackList = new Thread[estimatedSize];

		// Load the thread references into the oversized
		// array. The actual number of threads loaded 
		// is returned.
		int actualSize = topGroup.enumerate(slackList);

		// copy into a list that is the exact size
		Thread[] list = new Thread[actualSize];
		System.arraycopy(slackList,0,list,0,actualSize);

		return list;
	}

	public static void printThreadDump()
	{
		Thread[] rt = findAllThreads();

		System.out.println("---------------------Thread Dump-----------------------");
		System.out.println("Name, Priority, Group");
		for (int i = 0; i < rt.length; i++)
		{
			Thread t = rt[i];

			System.out.print(t.getName());
			System.out.print(", ");
			System.out.print(t.getPriority());
			System.out.print(", ");
			ThreadGroup tg = t.getThreadGroup();
			String tgn;
			if (tg != null)
				tgn = tg.getName();
			else
				tgn = "null";
			System.out.println(tgn);
		}
		System.out.println("-------------------End Thread Dump---------------------");
	}
}
