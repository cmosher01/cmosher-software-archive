import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.surveysampling.util.StringFieldizer;

public class NameParser
{
    public static void main(String[] args) throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String s = br.readLine();
        while (s != null)
        {
            StringFieldizer sf = new StringFieldizer(s,'\t');
            String of = "";
            String f = "";
            if (sf.hasMoreTokens())
            {
                f = sf.nextToken();
                int lastspace = f.lastIndexOf(' ');
                if (lastspace < 0)
                    of = f;
                else
                    of = f.substring(lastspace+1);

                if (
                of.length()==1||
                of.equalsIgnoreCase("jr")||
                of.equalsIgnoreCase("sr")||
                of.equalsIgnoreCase("ii")||
                of.equalsIgnoreCase("iii")||
                of.equalsIgnoreCase("iv")||
                of.equalsIgnoreCase("11")||
                of.equalsIgnoreCase("111")
                )
                {
                    int nexttolastspace = f.lastIndexOf(' ',lastspace-1);
                    if (nexttolastspace >= 0)
                        of = f.substring(nexttolastspace+1,lastspace);
                }
            }
            System.out.print(f);
            while (sf.hasMoreTokens())
            {
                System.out.print('\t');
                System.out.print(sf.nextToken());
            }
            System.out.print('\t');
            System.out.print(of);
            if (of.length() <= 2)
                System.err.println("check surname: "+of);
            System.out.println();

            s = br.readLine();
        }
        System.out.flush();
        br.close();
    }
}
