import java.util.ArrayList;

public final class CollectionTests
{
    private CollectionTests() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    public static void main(String rArg[]) throws Throwable
    {
        ArrayList somelist = new ArrayList();
        somelist.add("test one");
        somelist.add(new Integer(4));
        somelist.add("test three");

        String[] rs = (String[])somelist.toArray(new String[somelist.size()]);

        for (int i = 0; i < rs.length; ++i)
        {
            String string = rs[i];
            System.out.println(string);
        }
    }
}
