package nu.mine.mosher.grodb;

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

    public void testRelationTypeSource()
    {
    	assertEquals("cites",RelationTypeSource.CITES.toString());
    }
}
