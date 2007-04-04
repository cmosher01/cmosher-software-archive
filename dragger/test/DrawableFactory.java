import java.awt.Shape;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import nu.mine.mosher.gedcom.Gedcom;
import nu.mine.mosher.gedcom.GedcomLine;
import nu.mine.mosher.gedcom.GedcomTag;
import nu.mine.mosher.gedcom.GedcomTree;
import nu.mine.mosher.gedcom.date.parser.GedcomDateValueParser;
import nu.mine.mosher.gedcom.date.parser.ParseException;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.grodb.date.DatePeriod;
import nu.mine.mosher.util.TreeNode;
import ui.DrawableFamily;
import ui.DrawablePersona;

/*
 * Created on May 27, 2006
 */
public class DrawableFactory
{
	private static final Point2D ZERO_ZERO = new Point2D.Double(0,0);

	private List<DrawablePersona> rPersona;
	private List<DrawableFamily> rFamily;

	private final Random rnd = new Random();
	private Map<String,DrawablePersona> mapIDtoPersona = new HashMap<String,DrawablePersona>();

	public void create() throws InvalidLevel, IOException
	{
		final BufferedReader gedcom = new BufferedReader(new InputStreamReader(new FileInputStream(new File("C:/genealogy/mosher.ged"))));
		final GedcomTree gt = Gedcom.readTree(gedcom);
		gedcom.close();

		final Collection<TreeNode<GedcomLine>> rNodeTop = new ArrayList<TreeNode<GedcomLine>>();
		gt.getRoot().getChildren(rNodeTop);

		this.rPersona = new ArrayList<DrawablePersona>();
		for (final TreeNode<GedcomLine> nodeTop : rNodeTop)
		{
			final GedcomLine lineTop = nodeTop.getObject();
			final GedcomTag tagTop = lineTop.getTag();

			switch (tagTop)
			{
				case INDI:
					parseIndividual(nodeTop);
				break;
			}
		}

		this.rFamily = new ArrayList<DrawableFamily>();
		for (final TreeNode<GedcomLine> nodeTop : rNodeTop)
		{
			final GedcomLine lineTop = nodeTop.getObject();
			final GedcomTag tagTop = lineTop.getTag();

			switch (tagTop)
			{
				case FAM:
					parseFamily(nodeTop);
				break;
			}
		}

//		List<DrawablePersona> rc;
//
//		final DrawablePersona p1 = createPersona(1);
//		final DrawablePersona p2 = createPersona(2);
//		final DrawablePersona p3 = createPersona(3);
//		final DrawablePersona p4 = createPersona(4);
//		final DrawablePersona p5 = createPersona(5);
//
//		rc = new ArrayList<DrawablePersona>();
//		rc.add(p3);
//		rc.add(p4);
//		rc.add(p5);
//		final DrawableFamily f1 = new DrawableFamily(p1,p2,rc);
//
//		this.rPersona.add(p1);
//		this.rPersona.add(p2);
//		this.rPersona.add(p3);
//		this.rPersona.add(p4);
//		this.rPersona.add(p5);
//
//		this.rFamily.add(f1);
	}

	private void parseFamily(TreeNode<GedcomLine> nodeFam)
	{
		DrawablePersona parentLeft = null;
		DrawablePersona parentRight = null;
		final List<DrawablePersona> rChild = new ArrayList<DrawablePersona>();

		final Collection<TreeNode<GedcomLine>> rItem = new ArrayList<TreeNode<GedcomLine>>();
		nodeFam.getChildren(rItem);
		for (final TreeNode<GedcomLine> item : rItem)
		{
			final GedcomLine line = item.getObject();
			final GedcomTag tag = line.getTag();

			switch (tag)
			{
				case HUSB:
					parentLeft = this.mapIDtoPersona.get(line.getPointer());
				break;
				case WIFE:
					parentRight = this.mapIDtoPersona.get(line.getPointer());
				break;
				case CHIL:
					rChild.add(this.mapIDtoPersona.get(line.getPointer()));
				break;
			}
		}

		try
		{
			this.rFamily.add(new DrawableFamily(parentLeft,parentRight,rChild));
		}
		catch (final Throwable e)
		{
			throw new IllegalStateException("Encountered the following error when parsing family with ID "+nodeFam.getObject().getID(),e);
		}
	}

	private void parseIndividual(final TreeNode<GedcomLine> nodeIndi)
	{
		String name = "[unknown]";
		String birth = "";
		String death = "";
		Point2D upperLeft = ZERO_ZERO;

		final Collection<TreeNode<GedcomLine>> rItem = new ArrayList<TreeNode<GedcomLine>>();
		nodeIndi.getChildren(rItem);
		for (final TreeNode<GedcomLine> item : rItem)
		{
			final GedcomLine line = item.getObject();
			final GedcomTag tag = line.getTag();

			switch (tag)
			{
				case NAME:
				{
					name = parseName(line.getValue());
				}
				break;
				case BIRT:
				{
					final String d = parseDatePlace(item);
					if (d.length() > 0)
					{
						birth = "b. "+d;
					}
				}
				break;
				case DEAT:
				{
					final String d = parseDatePlace(item);
					if (d.length() > 0)
					{
						death = "d. "+d;
					}
				}
				break;
				case UNKNOWN:
				{
					if (line.getTagString().equalsIgnoreCase("_XY"))
					{
						upperLeft = parseXY(line.getValue());
					}
				}
				break;
			}
		}
		final List<AttributedCharacterIterator> rLabel = new ArrayList<AttributedCharacterIterator>();
		rLabel.add(new AttributedString(name).getIterator());
		if (birth.length() > 0)
		{
			rLabel.add(new AttributedString(birth).getIterator());
		}
		if (death.length() > 0)
		{
			rLabel.add(new AttributedString(death).getIterator());
		}

		final DrawablePersona persona = new DrawablePersona(upperLeft,rLabel);
		this.rPersona.add(persona);

		this.mapIDtoPersona.put(nodeIndi.getObject().getID(),persona);
	}

	private String parseDatePlace(final TreeNode<GedcomLine> node)
	{
		String date = "";
		String place = "";

		final Collection<TreeNode<GedcomLine>> rItem = new ArrayList<TreeNode<GedcomLine>>();
		node.getChildren(rItem);
		for (final TreeNode<GedcomLine> item : rItem)
		{
			final GedcomLine line = item.getObject();
			final GedcomTag tag = line.getTag();

			switch (tag)
			{
				case DATE:
					date = parseDate(line.getValue());
				break;
				case PLAC:
					place = parsePlace(line.getValue());
				break;
			}
		}
		final StringBuilder sb = new StringBuilder();
		sb.append(date);
//		if (date.length() > 0 && place.length() > 0)
//		{
//			sb.append(", ");
//		}
//		sb.append(place);
		return sb.toString();
	}

	private String parsePlace(final String value)
	{
		return value;
	}

	private String parseDate(final String value)
	{
		DatePeriod date = null;

		final GedcomDateValueParser parser = new GedcomDateValueParser(new StringReader(value));
		try
		{
			parser.DateValue();
			date = parser.getPeriod();
		}
		catch (final ParseException e)
		{
			e.printStackTrace();
		}
		return date.toString();
	}

	private Point2D parseXY(final String value)
	{
		final StringTokenizer st = new StringTokenizer(value);
		final double x = Double.parseDouble(st.nextToken());
		final double y = Double.parseDouble(st.nextToken());
		return new Point2D.Double(x,y);
	}

	private static String parseName(final String value)
	{
		return value;
	}

	private DrawablePersona createPersona(int i)
	{
		final double x = rnd.nextInt(800);
		final double y = rnd.nextInt(600);
		final Point2D upperLeft = new Point2D.Double(x,y);

		final List<AttributedCharacterIterator> rLabel = new ArrayList<AttributedCharacterIterator>();

		final String text = "\u05D0\u05E0\u05D9 Hello \u05DC\u05D0 \u05DE\u05D1\u05D9\u05DF \u05E2\u05D1\u05E8\u05D9\u05EA Arabic \u0644\u0645\u062C\u0645\u0648\u0639\u0629";
		final AttributedString s = new AttributedString(""+i+". "+text);
		rLabel.add(s.getIterator());

		final String textB = "b. 1630";
		final AttributedString sB = new AttributedString(textB);
		rLabel.add(sB.getIterator());

		final String textD = "d. 1673";
		final AttributedString sD = new AttributedString(textD);
		rLabel.add(sD.getIterator());

		return new DrawablePersona(upperLeft,rLabel);
	}

	public void getPersona(final Collection<DrawablePersona> append)
	{
		append.addAll(this.rPersona);
	}

	public void getFamily(final Collection<DrawableFamily> append)
	{
		append.addAll(this.rFamily);
	}
}
