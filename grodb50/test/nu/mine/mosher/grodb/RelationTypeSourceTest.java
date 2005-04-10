package nu.mine.mosher.grodb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

public class RelationTypeSourceTest extends TestCase
{
    public RelationTypeSourceTest(String name)
    {
        super(name);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(RelationTypeSourceTest.class);
    }

    public void testRelationTypeSource() throws IOException, ClassNotFoundException
    {
//    	assertEquals("cites",RelationTypeSource.CITES.toString());
//
//		ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
//		ObjectOutputStream oos = new ObjectOutputStream(baos);
//		oos.writeObject(RelationTypeSource.CITES);
//		byte[] rb = baos.toByteArray();
//		oos.close();
//
//		ByteArrayInputStream bais = new ByteArrayInputStream(rb);
//		ObjectInputStream ois = new ObjectInputStream(bais);
//		Object obj = ois.readObject();
//		assertTrue(obj instanceof RelationTypeSource);
//		RelationTypeSource srel = (RelationTypeSource)obj;
//		assertEquals(RelationTypeSource.CITES,srel);
    }
}
