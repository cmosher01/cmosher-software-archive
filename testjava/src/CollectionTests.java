import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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



        // converting from a List to an array
        String[] rs = (String[])somelist.toArray(new String[somelist.size()]);

        for (int i = 0; i < rs.length; ++i)
        {
            String string = rs[i];
            System.out.println(string);
        }


        //converting from an Iterator to a List
        Iterator t = new Iterator()
        {
            int c = 3;
            int i;
            public boolean hasNext()
            {
                return i < c;
            }

            public Object next() throws NoSuchElementException
            {
                if (!hasNext())
                {
                    throw new NoSuchElementException();
                }
                return Integer.toString(i);
            }

            public void remove() throws UnsupportedOperationException
            {
                throw new UnsupportedOperationException();
            }
        };
    }
}
