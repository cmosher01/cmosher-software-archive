import java.util.ArrayList;
import java.util.List;

public final class CollectionTests
{
    private CollectionTests() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    public static void main(String rArg[]) throws Throwable
    {
        List somelist = new ArrayList();
        somelist.add("test one");
        somelist.add("test two");
        somelist.add("test three");

        // converting from a List to and array
        String[] rs = (String[])somelist.toArray(new String[somelist.size()]);

        for (int i = 0; i < rs.length; ++i)
        {
            String string = rs[i];
            System.out.println(string);
        }
    }
}
