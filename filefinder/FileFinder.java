import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileFinder
{
    public static void main(String[] rArg) throws Throwable
    {
        if (rArg.length < 2 || 3 < rArg.length)
        {
            System.err.println("Usage: java FileFinder dir file-reg-exp [subdir-reg-exp]");
            System.exit(1);
        }

        final File dir = new File(rArg[0]);
        final Pattern patFile = Pattern.compile(rArg[1]);
        String sub = "";
        if (rArg.length > 2)
        {
            sub = rArg[2];
        }
        final Pattern patSubdir = Pattern.compile(sub);

        File[] rm = dir.listFiles(new FileFilter()
        {
            public boolean accept(File pathname)
            {
                Matcher mat = pat.matcher(pathname.getName());
                return mat.matches();
            }
        });
    }
}
