import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SplitLogs
{
    private static final DateFormat fmtDirYear = new SimpleDateFormat("yyyy");
    private static final DateFormat fmtDirDate = new SimpleDateFormat("MM-dd");
    private static final DateFormat fmtLogDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final boolean APPEND = true;

    public static void main(final String... rArg) throws IOException
    {
        if (rArg.length != 2)
        {
            throw new IllegalArgumentException("usage: java SplitLogs input-logfile output-dir-root");
        }
        final File fileIn = new File(rArg[0]).getCanonicalFile();
        if (!fileIn.canRead())
        {
            throw new IllegalArgumentException("Cannot read from input-logfile: "+fileIn);
        }
        final File rootDirOut = new File(rArg[1]).getCanonicalFile();
        if (!rootDirOut.canWrite())
        {
            throw new IllegalArgumentException("Cannot write to output-dir-root: "+rootDirOut);
        }



        final String logName = filterName(fileIn.getName());

        final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn)));
        for (String lineIn = in.readLine(); lineIn != null; lineIn = in.readLine())
        {
            final Date logDate;
            try
            {
                final String strLogDate = lineIn.substring(0,19);
                // kludge the year:
//                final String y;
//                if (lineIn.startsWith("Dec"))
//                {
//                    y = "2006 ";
//                }
//                else
//                {
//                    y = "2007 ";
//                }
                logDate = fmtLogDate.parse(strLogDate);
            }
            catch (final Throwable e)
            {
                System.err.println("invalid date; SKIPPING: "+lineIn);
                continue;
            }

            final String strDirYear = fmtDirYear.format(logDate);
            final File dirYear = new File(rootDirOut,strDirYear);
            final String strDirDate = fmtDirDate.format(logDate);
            final File dirDate = new File(dirYear,strDirDate);
            dirDate.mkdirs();

            final File fileOut = new File(dirDate,logName);
            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOut,APPEND)));
            out.write(lineIn);
            out.newLine();
            out.flush();
            out.close();
        }

        in.close();
    }

    public static String filterName(final String baseName)
    {
        return baseName.replaceAll("\\.[0-9]*$","");
    }
}
