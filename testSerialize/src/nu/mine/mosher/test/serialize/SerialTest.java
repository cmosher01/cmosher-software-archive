/*
 * Created on Aug 5, 2005
 */
package nu.mine.mosher.test.serialize;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Test <code>Serializable</code>.
 *
 * @author Chris Mosher
 */
public class SerialTest implements Serializable
{
	private static final long serialVersionUID = 0x5a5a5a5a5a5a5a5aL;
	private int x = 7; // x==5 is what was serialized, and so (x == 5)
	//private final int x = 7; // if we make it final, read-in value doesn't override this, so (x == 7)
	private int y = 6; // this doesn't take (y == 0)
	private boolean valid = true; // this doesn't take (valid == false)
	private final boolean valid2 = true; // final, so it takes (valid2 == true)
	private boolean valid3; // not final, initialized to default (valid3 == false)
	private final boolean valid4 = false; // (valid4 == false)
	private volatile float junk = 8.9F; // doesn't take (junk == 0.0F)

	private SerialTest()
	{
		System.out.println("in constructor");
	}
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
//		final SerialTest s = new SerialTest();
//		final ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File("x.x")));
//		os.writeObject(s);
//		os.flush();
//		os.close();
		final ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File("x.x")));
		final SerialTest s = (SerialTest)is.readObject();
		System.out.println(s);
	}

	@Override
	public String toString()
	{
		return "x: "+this.x+
		"\ny: "+this.y+
		"\nvalid: "+(this.valid ? "yes" : "no")+
		"\nvalid2: "+(this.valid2 ? "yes" : "no")+
		"\nvalid3: "+(this.valid3 ? "yes" : "no")+
		"\nvalid4: "+(this.valid4 ? "yes" : "no")+
		"\njunk: "+this.junk;
	}
}
