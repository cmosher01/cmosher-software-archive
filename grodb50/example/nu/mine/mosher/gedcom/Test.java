package nu.mine.mosher.gedcom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;
import nu.mine.mosher.gedcom.date.parser.GedcomDateValueParser;
import nu.mine.mosher.gedcom.date.parser.ParseException;
import nu.mine.mosher.grodb.date.DatePeriod;
import nu.mine.mosher.grodb2.datatype.DatePeriodBinding;
import nu.mine.mosher.grodb2.datatype.Event;
import nu.mine.mosher.grodb2.datatype.EventID;
import nu.mine.mosher.grodb2.datatype.EventType;
import nu.mine.mosher.grodb2.datatype.Persona;
import nu.mine.mosher.grodb2.datatype.PersonaID;
import nu.mine.mosher.grodb2.datatype.PlaceID;
import nu.mine.mosher.grodb2.datatype.Role;
import nu.mine.mosher.grodb2.datatype.RoleID;
import nu.mine.mosher.grodb2.datatype.RoleType;
import nu.mine.mosher.grodb2.datatype.Source;
import nu.mine.mosher.grodb2.datatype.SourceBinding;
import nu.mine.mosher.grodb2.datatype.SourceID;
import nu.mine.mosher.grodb2.datatype.SourceIDBinding;
import nu.mine.mosher.grodb2.datatype.Surety;
import nu.mine.mosher.grodb2.datatype.TimeBinding;
import nu.mine.mosher.grodb2.datatype.Transcript;
import nu.mine.mosher.grodb2.datatype.UUIDBinding;
import nu.mine.mosher.time.Time;
import nu.mine.mosher.util.TreeNode;
import nu.mine.mosher.uuid.UUIDFactory;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class Test
{
	/**
	 * Use this to generate UUIDs.
	 */
	public static final UUIDFactory factoryUUID = new UUIDFactory(true);



	/**
	 * @param rArg
	 * @throws Throwable
	 */
	public static void main(String[] rArg) throws Throwable
	{
		if (!TestAccess.environmentExists())
		{
			System.err.println("Database environment not found; creating...");
			TestAccess.createEnvironment();
		}

		System.err.println("Opening databases...");
		final Environment environment = TestAccess.openEnvironment(true);
		final Database dbSource = TestAccess.openDatabaseSource(environment,true);

		System.err.println("Parsing GEDCOM file...");

		final BufferedReader gedcom = new BufferedReader(new InputStreamReader(new FileInputStream(new File(/**/"C:/genealogy/testing.ged"/*"tgc55c.ged"/**/))));
		final GedcomTree gt = Gedcom.readTree(gedcom);
		gedcom.close();

		// create a map of IDs of top-level records found in the file, to our generated UUIDs
		System.err.println("Finding all IDs...");
		final Map<String,UUID> mapIDtoUUID = new HashMap<String,UUID>();
		createIDmapping(gt,mapIDtoUUID);

		// extract (non-top-level) SOUR records (i.e., embedded ones, as opposed to pointing ones or pointed-to ones)
		// create first class sources out of them
		final Map<String,SourceID> mapEmbeddedSourceToUUID = new HashMap<String,SourceID>();
		parseEmbeddedSources(gt.getRoot(),mapEmbeddedSourceToUUID);

		final Map<String,PlaceID> mapPlacetoUUID = new HashMap<String,PlaceID>();
		parsePlaces(gt.getRoot(),mapPlacetoUUID);

		final Collection<TreeNode<GedcomLine>> rNodeTop = new ArrayList<TreeNode<GedcomLine>>();
		gt.getRoot().getChildren(rNodeTop);
		for (final TreeNode<GedcomLine> nodeTop : rNodeTop)
		{
			final GedcomLine lineTop = nodeTop.getObject();
			final GedcomTag tagTop = lineTop.getTag();

			switch (tagTop)
			{
//				case INDI:
//					parseIndividual(nodeTop,mapIDtoUUID,mapPlacetoUUID,mapEmbeddedSourceToUUID);
//				break;
//				case FAM:
//					System.out.print("family: ");
//					System.out.println(line.getID());
//				break;
				case SOUR:
					parseSource(nodeTop,mapIDtoUUID,dbSource);
				break;

				default:
					System.out.println("SKIPPING TOP-LEVEL TAG "+tagTop);
			}
		}

		dbSource.close();
		TestAccess.closeEnvironment(environment);
	}

	private static void parseEmbeddedSources(final TreeNode<GedcomLine> root, final Map<String,SourceID> mapEmbeddedSourceToUUID)
	{
		final Collection<TreeNode<GedcomLine>> rNodeTop = new ArrayList<TreeNode<GedcomLine>>();
		root.getChildren(rNodeTop);
		for (final TreeNode<GedcomLine> node : rNodeTop)
		{
			final GedcomLine line = node.getObject();
			final GedcomTag tag = line.getTag();
			if (tag.equals(GedcomTag.HEAD))
			{
				// HEAD contains a SOUR, but not the right kind, so skip it
				continue;
			}

			if (tag.equals(GedcomTag.SOUR) && !line.hasID() && !line.isPointer())
			{
				System.out.println("extracting embedded source record: "+line.getValue());
				mapEmbeddedSourceToUUID.put(line.getValue(),new SourceID(Test.factoryUUID.createUUID()));
				// TODO write extracted Source
			}

			parseEmbeddedSources(node,mapEmbeddedSourceToUUID);
		}
	}

	private static void parsePlaces(final TreeNode<GedcomLine> root, final Map<String,PlaceID> mapPlaceToUUID)
	{
		final Collection<TreeNode<GedcomLine>> rNodeTop = new ArrayList<TreeNode<GedcomLine>>();
		root.getChildren(rNodeTop);
		for (final TreeNode<GedcomLine> node : rNodeTop)
		{
			final GedcomLine line = node.getObject();
			final GedcomTag tag = line.getTag();
			if (tag.equals(GedcomTag.HEAD))
			{
				// HEAD contains a PLAC, but not the right kind, so skip it
				continue;
			}

			if (tag.equals(GedcomTag.PLAC))
			{
				String place = line.getValue();
				place = cleanPlace(place);
				if (place.length() > 0)
				{
					if (!mapPlaceToUUID.containsKey(place))
					{
						mapPlaceToUUID.put(place,new PlaceID(Test.factoryUUID.createUUID()));
					}
				}
			}

			parsePlaces(node,mapPlaceToUUID);
		}
	}


	private static String cleanPlace(final String place)
	{
		final StringBuilder sb = new StringBuilder();
		final StringTokenizer st = new StringTokenizer(place,",");
		while (st.hasMoreTokens())
		{
			final String placePart = st.nextToken();
			if (sb.length() > 0)
			{
				sb.append(",");
			}
			sb.append(placePart.trim());
		}
		return sb.toString();
	}

	private static void parseIndividual(final TreeNode<GedcomLine> nodeTop, final Map<String,UUID> mapIDtoUUID, final Map<String,PlaceID> mapPlacetoUUID, final Map<String,SourceID> mapEmbeddedSourceToUUID)
	{
		final GedcomLine lineTop = nodeTop.getObject();

		System.out.print("individual: ");
		System.out.println(lineTop.getID());

		final List<TreeNode<GedcomLine>> rNodeName = new ArrayList<TreeNode<GedcomLine>>();
		getChildrenOfType(nodeTop,GedcomTag.NAME,rNodeName);
		if (rNodeName.size() > 1)
		{
			System.out.println("Multiple names not yet supported."); // TODO handle multiple names
		}

		if (rNodeName.size() < 1)
		{
			System.err.println("No name found.");
			return;
		}

		// from NAME of INDI
		final Persona personaName = new Persona(rNodeName.get(0).getObject().getValue());
		SourceID idSourceName = null; // SOUR of NAME of INDI

		final List<TreeNode<GedcomLine>> rNodeSour = new ArrayList<TreeNode<GedcomLine>>();
		getChildrenOfType(rNodeName.get(0),GedcomTag.SOUR,rNodeSour);
		if (rNodeSour.size() > 1)
		{
			System.out.println("Multiple sources for name not supported."); // TODO handle multiple sources per name
		}
		if (rNodeSour.size() < 1)
		{
			System.err.println("Source not found: "+rNodeSour.get(0).getObject().getValue());
		}
		else
		{
			idSourceName = new SourceID(pointsToUUID(rNodeSour.get(0),mapIDtoUUID));
		}

		final List<TreeNode<GedcomLine>> rNodeEvent = new ArrayList<TreeNode<GedcomLine>>();
		getChildrenOfTypes(nodeTop,GedcomTag.setIndividualEvent,rNodeEvent);

		final Map<SourceID,PersonaID> mapSourceToPersona = new HashMap<SourceID,PersonaID>();
		for (final TreeNode<GedcomLine> node: rNodeEvent)
		{
			final TreeNode<GedcomLine> nodePlace = getChildOfTypeOrNull(node,GedcomTag.PLAC);
			final PlaceID idPlace;
			if (nodePlace != null)
			{
				final GedcomLine linePlace = nodePlace.getObject();
				final String cleanPlace = cleanPlace(linePlace.getValue());
				idPlace = mapPlacetoUUID.get(cleanPlace);
			}
			else
			{
				idPlace = null;
			}

			DatePeriod date = null;
			final TreeNode<GedcomLine> nodeDate = getChildOfTypeOrNull(node,GedcomTag.DATE);
			if (nodeDate != null)
			{
				final String sDate = nodeDate.getObject().getValue();
				GedcomDateValueParser parser = new GedcomDateValueParser(new StringReader(sDate));
				try
				{
					parser.DateValue();
					date = parser.getPeriod();
				}
				catch (final ParseException e)
				{
					e.printStackTrace();
				}
			}

			final List<TreeNode<GedcomLine>> rNodeEventSour = new ArrayList<TreeNode<GedcomLine>>();
			getChildrenOfType(node,GedcomTag.SOUR,rNodeEventSour);
			for (final TreeNode<GedcomLine> nodeEventSour: rNodeEventSour)
			{
				final GedcomLine lineSour = nodeEventSour.getObject();
				final SourceID idSour;
				if (lineSour.isPointer())
				{
					idSour = new SourceID(mapIDtoUUID.get(lineSour.getPointer()));
				}
				else // embedded source
				{
					idSour = mapEmbeddedSourceToUUID.get(lineSour.getValue());
				}

				final PersonaID idPersona;
				if (mapSourceToPersona.containsKey(idSour))
				{
					idPersona = mapSourceToPersona.get(idSour);
				}
				else
				{
					idPersona = new PersonaID(Test.factoryUUID.createUUID());
				}

				final GedcomLine lineEvent = node.getObject();
				final EventType eventtype = EventType.fromGedcom(lineEvent.getTag());
				final Event event = new Event(eventtype,eventtype == EventType.other ? lineEvent.getValue() : "",date,new Surety(0),idPlace,new Surety(0));
				final EventID eventID = new EventID(Test.factoryUUID.createUUID());

				final Role role = new Role(RoleType.actor/*TODO fix role type*/,"",new Surety(0),idPersona,eventID);
				final RoleID roleID = new RoleID(Test.factoryUUID.createUUID());

				
			}
		}
		//Assertion assertion = new Assertion();
	}



	private static UUID pointsToUUID(final TreeNode<GedcomLine> nodePointer, final Map<String,UUID> mapIDtoUUID)
	{;
		final GedcomLine line = nodePointer.getObject();
		final UUID uuid = mapIDtoUUID.get(line.getPointer());
		if (uuid == null)
		{
			System.err.println("Cannot find pointed-to node, in line: "+line);
		}
		return uuid;
	}

	private static void parseSource(final TreeNode<GedcomLine> nodeTop, final Map<String,UUID> mapIDtoUUID, final Database dbSource) throws DatabaseException
	{
		final GedcomLine lineTop = nodeTop.getObject();

		System.out.print("source: ");
		System.out.println(lineTop.getID());

		String title = null;
		String author = null;
		String publisher = null;
		String doc = null;

		final Collection<TreeNode<GedcomLine>> rNode = new ArrayList<TreeNode<GedcomLine>>();
		nodeTop.getChildren(rNode);
		for (final TreeNode<GedcomLine> node : rNode)
		{
			final GedcomLine line = node.getObject();
			final GedcomTag tag = line.getTag();
//						if (.getTag().equals(GedcomTag.TEXT))
//						{
//							System.out.print("[TEXT, len: ");
//							System.out.print(node.getObject().getValue().length());
//							System.out.println("]");
//						}
//						else
//						{
//							System.out.print(node);
//						}

			switch (tag)
			{
				case TITL:
					if (title != null)
					{
						System.out.println("skipping duplicate TITL: "+line.getValue());
					}
					else
					{
						title = line.getValue();
					}
				break;
				case AUTH:
					if (author != null)
					{
						System.out.println("skipping duplicate AUTH: "+line.getValue());
					}
					else
					{
						author = line.getValue();
					}
				break;
				case PUBL:
					if (publisher != null)
					{
						System.out.println("skipping duplicate PUBL: "+line.getValue());
					}
					else
					{
						publisher = line.getValue();
					}
				break;
				case TEXT:
					if (doc != null)
					{
						System.out.println("skipping duplicate TEXT: "+line.getValue());
					}
					else
					{
						doc = line.getValue();
					}
				break;
				default:
					System.out.println("SKIPPING TAG "+tag+": "+line.getValue());
			}
		}
		if (title == null)
		{
			title = "[unknown]";
		}
		if (author == null)
		{
			author = "[unknown]";
		}
		if (publisher == null)
		{
			publisher = "[unknown]";
		}
		final SourceID id = new SourceID(mapIDtoUUID.get(lineTop.getID()));
		final Source source = new Source(title,author,new Time(new Date(0L)),"",publisher);

		final TransactionConfig confTxn = new TransactionConfig();
		confTxn.setReadCommitted(true);
		final Transaction txn = dbSource.getEnvironment().beginTransaction(TestAccess.NO_PARENT,confTxn);

		final SourceIDBinding bindingSourceID = new SourceIDBinding(new UUIDBinding());
		final SourceBinding bindingSource = new SourceBinding(new DatePeriodBinding(), new TimeBinding());

		final DatabaseEntry key = new DatabaseEntry();
		bindingSourceID.objectToEntry(id,key);

		final DatabaseEntry dat = new DatabaseEntry();
		bindingSource.objectToEntry(source,dat);

		TestAccess.put(key,dat,dbSource,txn);

		txn.commit();

		if (doc != null)
		{
			final Transcript transcript = new Transcript(doc);
			// TODO write Transcript
		}
//					System.out.println("source ID "+lineTop.getID()+": UUID: "+uuid+", title: "+title+", author: "+author);
//					System.out.println("publ: "+publisher);
	}

	private static void getChildrenOfType(final TreeNode<GedcomLine> nodeParent, final GedcomTag tagChildType, final Collection<TreeNode<GedcomLine>> appendTo)
	{
		final Collection<TreeNode<GedcomLine>> rNode = new ArrayList<TreeNode<GedcomLine>>();
		nodeParent.getChildren(rNode);

		for (final TreeNode<GedcomLine> node : rNode)
		{
			final GedcomLine line = node.getObject();
			final GedcomTag tag = line.getTag();
			if (tag.equals(tagChildType))
			{
				appendTo.add(node);
			}
		}
	}

	private static void getChildrenOfTypes(final TreeNode<GedcomLine> nodeParent, final Set<GedcomTag> setTagChildType, final Collection<TreeNode<GedcomLine>> appendTo)
	{
		final Collection<TreeNode<GedcomLine>> rNode = new ArrayList<TreeNode<GedcomLine>>();
		nodeParent.getChildren(rNode);

		for (final TreeNode<GedcomLine> node : rNode)
		{
			final GedcomLine line = node.getObject();
			final GedcomTag tag = line.getTag();
			if (setTagChildType.contains(tag))
			{
				appendTo.add(node);
			}
		}
	}

	private static TreeNode<GedcomLine> getChildOfTypeOrNull(final TreeNode<GedcomLine> nodeParent, final GedcomTag tagChildType)
	{
		TreeNode<GedcomLine> ret = null;

		final Collection<TreeNode<GedcomLine>> rNode = new ArrayList<TreeNode<GedcomLine>>();
		nodeParent.getChildren(rNode);

		for (final TreeNode<GedcomLine> node : rNode)
		{
			final GedcomLine line = node.getObject();
			final GedcomTag tag = line.getTag();
			if (tag.equals(tagChildType))
			{
				if (ret != null)
				{
					System.err.println("Found multiple tags of type "+tag+"; skipping: "+line.getValue());
				}
				else
				{
					ret = node;
				}
			}
		}

		return ret;
	}

	private static void createIDmapping(final GedcomTree gedcom, final Map<String,UUID> mapIDtoUUID)
	{
		final Collection<TreeNode<GedcomLine>> rNodeTop = new ArrayList<TreeNode<GedcomLine>>();
		gedcom.getRoot().getChildren(rNodeTop);
		for (final TreeNode<GedcomLine> nodeTop : rNodeTop)
		{
			final GedcomLine line = nodeTop.getObject();
			if (line.hasID())
			{
				mapIDtoUUID.put(line.getID(),Test.factoryUUID.createUUID());
			}
		}
//		for (final Map.Entry<String,UUID> entry : mapIDtoUUID.entrySet())
//		{
//			System.err.print("mapping ID ");
//			System.err.print(entry.getKey());
//			System.err.print(" to UUID ");
//			System.err.print(entry.getValue());
//			System.err.println();
//		}
	}
}
