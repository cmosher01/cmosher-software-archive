import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created on Nov 26, 2003
 */

/**
 * @author Chris Mosher
 */
public class ExtrDos33
{
    public static void main(String[] rArg) throws Throwable
    {
        if (rArg.length < 2 || 3 < rArg.length)
        {
            System.err.println("Usage: java FileFinder dir file-reg-exp [subdir-reg-exp]");
            System.exit(1);
        }

        String sDir = rArg[0];
        System.err.println("dir: " + sDir);

        String regexpFile = rArg[1];
        System.err.println("file-reg-exp: " + regexpFile);

        String regexpDir = "";
        if (rArg.length > 2)
        {
            regexpDir = rArg[2];
            System.err.println("subdir-reg-exp: " + regexpDir);
        }

        File[] rf = listRegFiles(sDir, regexpFile, regexpDir);

		File dirNew = new File(sDir,"extrdos33");
		if (dirNew.exists())
		{
			throw new Exception(dirNew.getCanonicalFile().getAbsolutePath()+" directory already exists");
		}

		if (!dirNew.mkdir())
		{
			throw new Exception("could not create directory "+dirNew.getCanonicalFile().getAbsolutePath());
		}

        for (int i = 0; i < rf.length; ++i)
        {
            File file = rf[i];
            extrDos(file,newDir);
        }
    }

    public static File[] listRegFiles(String sDir, String regexpFile, String regexpDir) throws IOException
    {
        final Pattern patFile = Pattern.compile(regexpFile);
        final Pattern patSubdir = Pattern.compile(regexpDir);

        final List dirs = new ArrayList();
        dirs.add(new File(sDir));

        List results = new ArrayList();

        while (dirs.size() > 0)
        {
            File d = (File)dirs.remove(0);
            File[] rm = d.listFiles(new FileFilter()
            {
                public boolean accept(File pathname)
                {
                    Matcher matDir = patSubdir.matcher(pathname.getName());
                    if (matDir.matches())
                    {
                        dirs.add(pathname);
                    }
                    Matcher matFile = patFile.matcher(pathname.getName());
                    return matFile.matches();
                }
            });
            for (int i = 0; rm != null && i < rm.length; ++i)
            {
                File file = rm[i];
                results.add(file.getCanonicalFile().getAbsoluteFile());
            }
        }
        return (File[])results.toArray(new File[results.size()]);
    }
}
