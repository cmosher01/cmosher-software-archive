import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.surveysampling.util.StringFieldizer;

public class FixedFieldizer
{
    public static void main(String[] args) throws Exception
    {
        int maxCol = countColumns(args[0]);

        if (maxCol==0)
            return;

        int[] widthCol = new int[maxCol];

        getColWidths(args[0],widthCol);

        fixColumns(args[0],widthCol);
    }

    private static void fixColumns(String fil, int[] widthCol) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(fil));
        String s = br.readLine();
        while (s != null)
        {
            StringFieldizer sf = new StringFieldizer(s,'\t');
            int iCol = 0;
            while (sf.hasMoreTokens())
            {
                String f = sf.nextToken();
                System.out.print(f);
                int dif = widthCol[iCol]-f.length();
                for (int i = 0; i < dif; ++i)
                    System.out.print(" ");
                ++iCol;
            }
            System.out.println();

            s = br.readLine();
        }
        br.close();
    }

    private static void getColWidths(String fil, int[] widthCol) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(fil));
        String s = br.readLine();
        while (s != null)
        {
            StringFieldizer sf = new StringFieldizer(s,'\t');
            int iCol = 0;
            while (sf.hasMoreTokens())
            {
                String f = sf.nextToken();
                if (f.length() > widthCol[iCol])
                    widthCol[iCol] = f.length();
                ++iCol;
            }

            s = br.readLine();
        }
        br.close();
    }

    private static int countColumns(String fil) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(fil));
        
        int maxCol = 0;
        
        String s = br.readLine();
        while (s != null)
        {
            int cCol = 1;
            for (int i = 0; i < s.length(); i++)
            {
                char c = s.charAt(i);
                if (c=='\t')
                    ++cCol;
            }
            if (cCol > maxCol)
                maxCol = cCol;
            s = br.readLine();
        }
        br.close();

        return maxCol;
    }
}
