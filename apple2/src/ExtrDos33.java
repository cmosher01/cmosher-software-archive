import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
    	for (int i = 0; i < rArg.length; ++i)
    	{
			System.err.println("arg "+i+": "+rArg[i]);
    	}
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
        System.out.println("=============================================================================");
        for (Iterator i = doss.keySet().iterator(); i.hasNext();)
        {
        	DosImage dos = (DosImage)i.next();
        	System.out.println(dos.dosFile.getAbsolutePath()+":");
        	dos.showSigs();
        	for (Iterator j = dos.files.iterator(); j.hasNext();)
            {
                File file = (File)j.next();
                System.out.println("    "+file.getAbsolutePath());
            }
        }
    }

	private static class DosImage
	{
		private int hash;
		File dosFile;
        List files = new ArrayList();
        byte[] rb;
		byte[] rbc;
		public DosImage(byte[] x)
		{
			rb = new byte[x.length];
			System.arraycopy(x,0,rb,0,x.length);
			rbc = new byte[x.length];
			System.arraycopy(x,0,rbc,0,x.length);

			// clear areas for comparison:

			// some unknown bytes that vary
			for (int i = 0x1EA; i < 0x1F9; ++i)
			{
				rbc[i] = 0;
			}

			// HELLO program type???
			rbc[0xD42] = 0;

			// (in most DOS 3.3's, but not Prontodos or ESdos) ??? :
			// some bytes before HELLO program name
			for (int i = 0x1959; i < 0x1975; ++i)
			{
				rbc[i] = 0;
			}
			// HELLO program name
			for (int i = 0x1975; i < 0x19B1; ++i)
			{
				rbc[i] = 0;
			}
			// clear other area (T$11 S$0 copy plus other stuff)
			for (int i = 0x2297; i < 0x2500; ++i)
			{
				rbc[i] = 0;
			}
		}
		public boolean hasSignature()
		{
			return (rb[0] == 0x00000001 && rb[1] == 0xFFFFFFA5 && rb[2] == 0x00000027 && rb[3] == 0xFFFFFFC9);
		}
		public boolean hasProtodosSignature()
		{
			return (rb[0x1602] == 0x54 && rb[0x1603] == 0x59 && rb[0x1604] == 0x50 && rb[0x1605] == 0xFFFFFFC5);
		}
		public boolean hasDaviddosSignature()
		{
			return (rb[0x1924] == 0x44 && rb[0x1925] == 0x2D && rb[0x1926] == 0x44 && rb[0x1927] == 0x4F && rb[0x1928] == 0x53);
		}
		public boolean hasDaviddos2Signature()
		{
			return hasDaviddosSignature() && 
			(rb[0x2701] == 0xFFFFFFC4 && rb[0x2702] == 0xFFFFFFC1 && rb[0x2703] == 0xFFFFFFD6 && rb[0x270B] == 0xFFFFFFC9 && rb[0x270C] == 0xFFFFFFC9);
		}
		public boolean hasDiversidos2cSignature()
		{
			return (rb[0xA10] == 0x20 && rb[0xA11] == 0xFFFFFF84 && rb[0xA12] == 0xFFFFFF9D && rb[0xA13] == 0xFFFFFFA0);
		}
		public boolean hasDiversidos41cSignature()
		{
			return (rb[0xA10] == 0xFFFFFFA9 && rb[0xA11] == 0xFFFFFFFF && rb[0xA12] == 0xFFFFFF8D && rb[0xA13] == 0xFFFFFFFB);
		}
		public boolean hasEsdosSignature()
		{
			return (rb[0x1671] == 0x4C && rb[0x1672] == 0x4E && rb[0x1673] == 0xFFFFFFC1 && rb[0x1674] == 0x52);
		}
		public boolean hasHyperdosSignature()
		{
			return (rb[0x656] == 0xFFFFFFAD && rb[0x657] ==  0x61 && rb[0x658] ==  0xFFFFFFAA && rb[0x659] ==  0xFFFFFFC9 && rb[0x65A] ==  0x01 && rb[0x65B] ==  0xFFFFFFB0);
		}
		public boolean hasRdosSignature()
		{
			return (rb[0x100] == 0x4C && rb[0x101] ==  0x74 && rb[0x102] ==  0xFFFFFFB9 && rb[0x103] ==  0xFFFFFFA0);
		}
        public boolean equals(Object obj)
        {
        	if (!(obj instanceof DosImage))
        	{
        		return false;
        	}
        	DosImage that = (DosImage)obj;
			return Arrays.equals(this.rbc,that.rbc);
        }
        public int hashCode()
        {
        	if (hash == 0)
        	{
				hash = 17;
				for (int i= 0; i < rbc.length; ++i)
				{
					hash *= 37;
					hash += rbc[i];
				}
        	}
			return hash;
        }
        public void addFile(File file)
        {
        	files.add(file);
        }
        public void setDosFile(File file)
        {
        	dosFile = file;
        }
        public void showSigs()
        {
			if (hasDaviddosSignature())
			{
				sig("David DOS");
			}
			if (hasDaviddos2Signature())
			{
				sig("David DOS II");
			}
			if (hasDiversidos2cSignature())
			{
				sig("Diversi-DOS 2-C");
			}
			if (hasDiversidos41cSignature())
			{
				sig("Diversi-DOS 4.1-C");
			}
			if (hasEsdosSignature())
			{
				sig("ES DOS");
			}
			if (hasHyperdosSignature())
			{
				sig("Hyper-DOS");
			}
			if (this.hasProtodosSignature())
			{
				sig("Pronto-DOS (Beagle Bros.)");
			}
			if (this.hasRdosSignature())
			{
				sig("RDOS (SSI)");
			}
        }
        private void sig(String s)
        {
        	System.out.println("-----> has "+s+" signature <-----");
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

		if (doss.containsKey(dos))
		{
			DosImage existDos = (DosImage)doss.get(dos);
			System.out.println("image already found: "+existDos.dosFile.getAbsolutePath());
			existDos.addFile(file);
		}
		else
		{
			dos.setDosFile(new File(dirNew,nextDosFileName()));
			System.out.println("New DOS 3.3 image. Saving Tracks $00-$03 to file: "+dos.dosFile.getAbsolutePath());
			dos.addFile(file);
			doss.put(dos,dos);
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dos.dosFile));
			out.write(dos.rb);
			out.flush();
			out.close();
		}


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
