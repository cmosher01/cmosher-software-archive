package nu.mine.mosher.gedcom;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

public class Test
{
	public static void main(String[] rArg) throws Throwable
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("tgc55c.ged"))));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("parsed.txt"))));
		GedcomParser gp = new GedcomParser(br);
		GedcomLine gl = null;
		GedcomTree gt = new GedcomTree();

//		try
//		{
			gl = gp.nextLine();
//		}
//		catch (GedcomParseException e)
//		{
//			throw e;
//		}
		while (gl != null)
		{
			gt.appendLine(gl);
			gl = gp.nextLine();
		}

		GedcomConcatenator gc = new GedcomConcatenator(gt);
		gc.concatenate();

		bw.write(gt.toString());

		bw.close();
		br.close();
	}
}
