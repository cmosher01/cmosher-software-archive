import java.lang.reflect.Method;
import java.util.Arrays;

/*
 * Created on Oct 2, 2006
 */
public class Play
{
	public void thisIsPlay() {}
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		final Play play = new Play();
		System.out.println(play.getClass().getName());
		final Class<? extends Play> clas = play.getClass();
		Class<? extends Class> clas2 = clas.getClass();

		final Method[] rMethod = clas2.getMethods();
		for (final Method method: Arrays.asList(rMethod))
		{
			final String nameMethod = method.getName();
			System.out.println(nameMethod);
		}
	}

	public static void doSome() throws Exception
	{
		throw new Exception();
	}
	public void testexc()
	{
	}
	protected void setOutput(String u) { }
}
