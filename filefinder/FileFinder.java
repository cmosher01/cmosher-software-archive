import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileFinder
{
    public static void main(String[] rArg) throws Throwable
    {
        if (rArg.length != 2)
        {
            System.err.println("Usage: java FileFinder dir file-reg-exp");
            System.exit(1);
        }

        final File dir = new File(rArg[0]);
        final Pattern pat = Pattern.compile(rArg[1]);
        dir.listFiles(new FileFilter()
        {
            public boolean accept(File pathname)
            {
                Matcher mat = pat.matcher(pathname.getName());
                return mat.matches();
            }
        });
    }
}
