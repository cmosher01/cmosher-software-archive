import java.io.BufferedReader;
import java.io.FileReader;

public class ReplaceColumnIf
{
    // define column widths for cols 0, 1, 2, and 3
    private static int[] rColWidth = {193, 84, 1, 84};

    /**
     * argument: name of file
     * replace col 1 with col 3 iff col 3 exists
     * output cols 0, 1', 2
     */
    public static void main(String[] args) throws Throwable
    {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));

        String rCol[] = new String[rColWidth.length];

        int tc = 0;
        for (int i = 0; i < rColWidth.length; ++i)
        {
            int c = rColWidth[i];
            tc += c;
        }

        String s = br.readLine();
        int crec = 0;
        while (s != null)
        {
            ++crec;
            if (s.length() != tc)
            {
                System.err.print("ERROR: invalid input length: ");
                System.err.println(s.length());
                System.err.print("at record ");
                System.err.println(crec);
                continue;
            }
            int at = 0;
            for (int i = 0; i < rColWidth.length; ++i)
            {
                rCol[i] = s.substring(at,at+rColWidth[i]);
                at += rColWidth[i];
            }

            if (rCol[3].trim().length() > 0)
                rCol[1] = rCol[3];

            for (int i = 0; i < rColWidth.length-1; ++i)
            {
                System.out.print(rCol[i]);
            }
            System.out.println();

            s = br.readLine();
        }

        br.close();
    }
}
