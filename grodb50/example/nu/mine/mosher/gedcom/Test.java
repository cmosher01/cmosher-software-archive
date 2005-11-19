package nu.mine.mosher.gedcom;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import nu.mine.mosher.util.TreeNode;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class Test
{
	/**
	 * @param rArg
	 * @throws Throwable
	 */
	public static void main(String[] rArg) throws Throwable
	{
		final Reader gedcom = new InputStreamReader(new FileInputStream(new File("tgc55c.ged")));
		final GedcomParser gp = new GedcomParser(gedcom);

		final GedcomTree gt = new GedcomTree();
		for (final GedcomLine line: gp)
		{
			gt.appendLine(line);
		}

		gedcom.close();

		final GedcomConcatenator gcat = new GedcomConcatenator(gt);
		gcat.concatenate();

//		final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("parsed2.txt"))));
//		bw.write(gt.toString());
//		bw.close();

		final Collection<TreeNode<GedcomLine>> rNodeTop = new ArrayList<TreeNode<GedcomLine>>();
		gt.getRoot().getChildren(rNodeTop);
		for (final TreeNode<GedcomLine> nodeTop : rNodeTop)
		{
			final GedcomLine line = nodeTop.getObject();

			final GedcomTag tag = line.getTag();
			switch (tag)
			{
				case INDI:
					System.out.print("individual: ");
					System.out.println(line.getID());
				break;
				case FAM:
					System.out.print("family: ");
					System.out.println(line.getID());
				break;
				default:
			}
		}
	}
}
