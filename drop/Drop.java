/*
 * Created on Nov 25, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Drop
{
	public static void main(String[] rArg) throws Throwable
	{
		System.out.print(rArg.length);
		System.out.println(" args:");
		for (int i = 0; i < rArg.length; ++i)
        {
            String arg = rArg[i];
            System.out.println(arg);
        }
        System.in.read();
	}
}
