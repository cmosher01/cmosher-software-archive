import java.util.ArrayList;
import java.util.List;


public class PlayGroovy
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		final List<String> r = new ArrayList<>(2048);
		Testing t = new Testing();
		t.foo(r);
		System.out.println("size: "+r.size());
	}

}
