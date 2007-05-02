package com.surveysampling.util;

/**
 * Contains methods to get a list of current threads.
 * 
 * @author Chris Mosher
 */
public class ThreadUtil
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

	/* Waits for Thread t to finish, regardless
	 * of whether someone tries to interrupt the current
	 * thread or not.
	 * @return true if the current thread was interrupted
	 */
	public static boolean joinUninterruptable(Thread t)
	{
		boolean interrupted = false;

		boolean joined = false;
		while (!joined)
		{
			try
			{
				t.join();
				joined = true;
			}
			catch (InterruptedException e)
			{
				/*
				 * If the current thread (not Thread t) is
				 * interrupted while waiting for Thread t to
				 * finish, then we end up here. If this happens,
				 * we continue waiting for Thread t to finish,
				 * and just set a flag to indicate that we did
				 * get interrupted. After Thread t finishes, and
				 * we exit the loop, we will return this flag.
				 */
				interrupted = true;
			}
		}

		return interrupted;
	}
}
