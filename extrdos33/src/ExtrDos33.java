import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		dirNew = dirNew.getCanonicalFile();
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
            extrDos(file,dirNew);
        }
    }

	private static class DosImage
	{
		byte[] rb;
		byte[] rbc;
		public DosImage(byte[] x)
		{
			rb = new byte[x.length];
			System.arraycopy(x,0,rb,0,x.length);
			rbc = new byte[x.length];
			System.arraycopy(x,0,rbc,0,x.length);
		}
		public boolean hasSignature()
		{
			return (rb[0] == 0x01 && rb[1] == 0xA5 && rb[2] == 0x27 && rb[3] == 0xC9);
		}
	}

	private static Map doss = new HashMap();
    public static void extrDos(File file, File dirNew) throws IOException
    {
		System.out.println("-----------------------------------------------------------------------------------");
    	System.out.println("Processing file: "+file.getAbsolutePath());

		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		byte[] rb = new byte[0x3000];
		if (in.read(rb) != rb.length)
		{
			System.out.println("Disk image doesn't have at least 3 tracks; skipping.");
			return;
		}

		DosImage dos = new DosImage(rb);
		if (!dos.hasSignature())
		{
			System.out.println("Track $00, Sector $0, Bytes $00-$03 are not: 01 A5 27 C9.");
			System.out.println("therefore this doesn't appear to be a DOS 3.3 disk image; skipping.");
			return;
		}

		byte[] rbc = new byte[rb.length];
		System.arraycopy(rb,0,rbc,0,rb.length);

		// clear HELLO program name for comparison
		for (int i = 0x1975; i < 0x19B1; ++i)
		{
			rbc[i] = 0;
		}

		if (doss.containsKey(rbc))
		File saveDos = new File(dirNew,nextDosFileName());
		System.out.println("Saving Tracks $00-$03 to file: "+saveDos.getAbsolutePath());

    }

	private static int nextDosFileNumber;
	public static String nextDosFileName()
	{
		++nextDosFileNumber;
		String s = Integer.toString(nextDosFileNumber);
		if (nextDosFileNumber < 1000)
		{
			s = "0"+s;
		}
		if (nextDosFileNumber < 100)
		{
			s = "0"+s;
		}
		if (nextDosFileNumber < 10)
		{
			s = "0"+s;
		}
		return s+".dos";
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
