import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * TODO
 *
 * Created on Apr 12, 2004
 */

/**
 * TODO
 */
public class TestThreadIO
{
    public static void main(String[] args)
    {
    }

    private void ReadFormFile() throws IOException
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
    }
}
