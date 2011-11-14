import java.util.Collection;
import java.util.Map;


public class DumpStacks
{
	public static void dumpStacks() throws InterruptedException
	{
		System.out.println("main start");
		Thread th = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				System.out.println("my start");
				Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
				Collection<StackTraceElement[]> values = allStackTraces.values();
				System.out.println("------------------------------------------------------------------------");
				for (StackTraceElement[] rs : values)
				{
					for (StackTraceElement s : rs)
					{
						System.out.println(s.getMethodName()+"   ["+s.getClassName()+"]");
					}
					System.out.println("------------------------------------------------------------------------");
				}
				System.out.println("my done");
			}
		}, "my thread");
		th.start();
		System.out.println("main waiting");
		th.join();
		System.out.println("main done");
	}
}
