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
        somelist.add("test two");
        somelist.add("test three");

        somelist.toArray(new String[somelist.size()]);
    }
}
