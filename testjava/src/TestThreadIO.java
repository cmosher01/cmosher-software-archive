import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestThreadIO
{
    private static final int THREADS = 2000;

    private static class ReadFormRunner implements Runnable
    {
        public void run()
        {
            try
            {
                ReadFormFile();
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws Throwable
    {
        List rt = new ArrayList();
        for (int i = 0; i < THREADS; ++i)
        {
            Thread t = new Thread(new ReadFormRunner());
            rt.add(t);
        }
        for (Iterator i = rt.iterator(); i.hasNext();)
        {
            Thread t = (Thread)i.next();
            t.start();
        }
        for (Iterator i = rt.iterator(); i.hasNext();)
        {
            Thread t = (Thread)i.next();
            t.join();
        }
    }

    public static void ReadFormFile() throws IOException
    {
        int numberOfEntries = 0;
        List lines = new ArrayList();

        String oneLine = "";
        int i = 0;
        int temp = 0;
        char cIn = ' ';

        BufferedInputStream input = new BufferedInputStream(new FileInputStream(new File("test.txt")));

        // Read the data into memory a line at a time

        // Read the current line to the buffer
        while (temp != -1)
        {
            cIn = ' ';
            oneLine = "";
            while ((cIn != '\n') && (temp != -1))
            {
                temp = input.read();
                cIn = (char)temp;
                if ((cIn != '\n') && (temp != -1))
                    oneLine += cIn;
            }

            // Add the data to the table
            if (oneLine.compareTo("") != 0)
            {
//                if (!AddFormRules(members, oneLine, i))
//                {
//                    return false;
//                }
                lines.add(oneLine);
                // Increment the number of entries
                numberOfEntries++;
            }
            i++;
        }
        if (numberOfEntries != 2)
        {
            System.err.println("numberOfEntries != 2");
        }
        if (lines.size() != 2)
        {
            System.err.println("lines.size() != 2");
        }
        else
        {
            String s = (String)lines.get(0);
            if (!s.equals("this is a test"))
            {
                System.err.println("s != this is a test");
            }
            s = (String)lines.get(1);
            if (!s.equals("this is also a test"))
            {
                System.err.println("s != this is also test");
            }
        }
    }
}
