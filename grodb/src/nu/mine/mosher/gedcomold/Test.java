package nu.mine.mosher.gedcomold;

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
		GedcomParser gp = new GedcomParser(br);

		GedcomTree gt = new GedcomTree();
		for (GedcomLine gl = gp.nextLine(); gl != null; gl = gp.nextLine())
		{
			gt.appendLine(gl);
		}

		br.close();

		GedcomConcatenator gcat = new GedcomConcatenator(gt);
		gcat.concatenate();

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("parsed.txt"))));
		bw.write(gt.toString());
		bw.close();
	}
}
