/*
 * Created on Aug 13, 2006
 */
package nu.mine.mosher.grodb.persist.gedcom;



import java.io.StringReader;
import java.text.ParseException;
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
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import nu.mine.mosher.gedcom.GedcomLine;
import nu.mine.mosher.gedcom.GedcomTag;
import nu.mine.mosher.gedcom.GedcomTree;
import nu.mine.mosher.gedcom.date.parser.GedcomDateValueParser;
import nu.mine.mosher.grodb.date.DatePeriod;
import nu.mine.mosher.grodb.persist.Event;
import nu.mine.mosher.grodb.persist.Persona;
import nu.mine.mosher.grodb.persist.Place;
import nu.mine.mosher.grodb.persist.Role;
import nu.mine.mosher.grodb.persist.Source;
import nu.mine.mosher.grodb.persist.Transcript;
import nu.mine.mosher.grodb.persist.key.EventID;
import nu.mine.mosher.grodb.persist.key.PersonaID;
import nu.mine.mosher.grodb.persist.key.PlaceID;
import nu.mine.mosher.grodb.persist.key.RoleID;
import nu.mine.mosher.grodb.persist.key.SourceID;
import nu.mine.mosher.grodb.persist.types.EventType;
import nu.mine.mosher.grodb.persist.types.RoleType;
import nu.mine.mosher.grodb.persist.types.Surety;
import nu.mine.mosher.time.Time;
import nu.mine.mosher.util.TreeNode;
import nu.mine.mosher.uuid.UUIDFactory;



public class GedcomImporter
{
	private static final UUIDFactory factoryUUID = new UUIDFactory();
	private static final Time ZERO_TIME;
	static
	{
		try
		{
			ZERO_TIME = Time.readFromString("");
		}
		catch (final ParseException e)
		{
			throw new IllegalStateException(e);
		}
	}



	private final GedcomTree gedcom;

	

	/**
	 * @param gedcom
	 */
	public GedcomImporter(final GedcomTree gedcom)
	{
		this.gedcom = gedcom;
	}

	public void importIntoStore(final EntityStore store) throws DatabaseException
	{
		// create mapping of all IDs from GEDCOM file to generated UUIDs
		final Map<String,UUID> mapIDtoUUID = new HashMap<String,UUID>(1024,Float.POSITIVE_INFINITY);
		createIDmapping(mapIDtoUUID);

		// extract (non-top-level) SOUR records (i.e., embedded ones, as opposed to pointing ones or pointed-to ones)
		// create first class sources out of them; keep a mapping of their description to our sourceID, so we can
		// link to the source record when we actually parse the embedded source later.
		final Map<String,SourceID> mapEmbeddedSourceToID = new HashMap<String,SourceID>(16,Float.POSITIVE_INFINITY);
		parseEmbeddedSources(this.gedcom.getRoot(),mapEmbeddedSourceToID,store);

		final Map<String,PlaceID> mapPlacetoID = new HashMap<String,PlaceID>(1024,Float.POSITIVE_INFINITY);
		parsePlaces(this.gedcom.getRoot(),mapPlacetoID,store);

		final Collection<TreeNode<GedcomLine>> rNodeTop = new ArrayList<TreeNode<GedcomLine>>();
		this.gedcom.getRoot().getChildren(rNodeTop);
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
					parseSource(nodeTop,mapIDtoUUID,store);
				break;

//				default:
//					System.out.println("SKIPPING TOP-LEVEL TAG "+tagTop);
			}
		}
	}




	private void createIDmapping(final Map<String,UUID> mapIDtoUUID)
	{
		final Collection<TreeNode<GedcomLine>> rNodeTop = new ArrayList<TreeNode<GedcomLine>>();
		this.gedcom.getRoot().getChildren(rNodeTop);

		for (final TreeNode<GedcomLine> nodeTop : rNodeTop)
		{
			final GedcomLine line = nodeTop.getObject();
			if (line.hasID())
			{
				mapIDtoUUID.put(line.getID(),factoryUUID.createUUID());
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



	private static void parseEmbeddedSources(final TreeNode<GedcomLine> root, final Map<String,SourceID> mapEmbeddedSourceToID, final EntityStore store) throws DatabaseException
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
				final String description = line.getValue();
//				System.out.println("extracting embedded source record: "+description);

				if (!mapEmbeddedSourceToID.containsKey(description))
				{
					final SourceID idSource = new SourceID(factoryUUID.createUUID());

					mapEmbeddedSourceToID.put(description,idSource);

					// take all of the "source description" and put it into the title (since there's no way we can parse it correctly)
					final Source source = new Source(idSource,description,"[see title]",ZERO_TIME,"[see title]","[see title]");
					final PrimaryIndex<SourceID,Source> pkSource = store.getPrimaryIndex(SourceID.class,Source.class);
					pkSource.putNoReturn(source);
				}


			}

			parseEmbeddedSources(node,mapEmbeddedSourceToID,store);
		}
	}



	private static void parsePlaces(final TreeNode<GedcomLine> root, final Map<String,PlaceID> mapPlaceToID, final EntityStore store) throws DatabaseException
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
				String placename = line.getValue();
				placename = cleanPlace(placename);
				if (placename.length() > 0)
				{
					if (!mapPlaceToID.containsKey(placename))
					{
						final PlaceID idPlace = new PlaceID(factoryUUID.createUUID());
						mapPlaceToID.put(placename,idPlace);

						final Place place = new Place(idPlace,"","",placename,"","",null);
						final PrimaryIndex<PlaceID,Place> pkPlace = store.getPrimaryIndex(PlaceID.class,Place.class);
						pkPlace.putNoReturn(place);
					}
				}
			}

			parsePlaces(node,mapPlaceToID,store);
		}
	}

	private static String cleanPlace(final String place)
	{
		final StringBuilder sb = new StringBuilder();
		final StringTokenizer st = new StringTokenizer(place,",");
		while (st.hasMoreTokens())
		{
			String placePart = st.nextToken();
			placePart = placePart.trim();

			if (sb.length() > 0)
			{
				sb.append(", ");
			}
			sb.append(placePart);
		}
		return sb.toString();
	}



	private static void parseSource(final TreeNode<GedcomLine> nodeTop, final Map<String,UUID> mapIDtoUUID, final EntityStore store) throws DatabaseException
	{
		final GedcomLine lineTop = nodeTop.getObject();

//		System.out.print("source: ");
//		System.out.println(lineTop.getID());

		String title = "";
		String author = "";
		String publisher = "";
		String doc = "";

		final Collection<TreeNode<GedcomLine>> rNode = new ArrayList<TreeNode<GedcomLine>>();
		nodeTop.getChildren(rNode);
		for (final TreeNode<GedcomLine> node : rNode)
		{
			final GedcomLine line = node.getObject();
			final GedcomTag tag = line.getTag();
			switch (tag)
			{
				case TITL:
					title += line.getValue();
				break;
				case AUTH:
					author += line.getValue();
				break;
				case PUBL:
					publisher += line.getValue();
				break;
				case TEXT:
					doc += line.getValue();
				break;
//				default:
//					System.out.println("SKIPPING TAG "+tag+": "+line.getValue());
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

		final SourceID idSource = new SourceID(mapIDtoUUID.get(lineTop.getID()));
		final Source source = new Source(idSource,title,author,ZERO_TIME,"",publisher);
		final PrimaryIndex<SourceID,Source> pkSource = store.getPrimaryIndex(SourceID.class,Source.class);
		pkSource.putNoReturn(source);

		if (doc != null && doc.length() > 0)
		{
			final Transcript transcript = new Transcript(idSource,doc);
			final PrimaryIndex<SourceID,Transcript> pkTranscript = store.getPrimaryIndex(SourceID.class,Transcript.class);
			pkTranscript.putNoReturn(transcript);
		}
	}



	private static void parseIndividual(final TreeNode<GedcomLine> nodeTop, final Map<String,UUID> mapIDtoUUID, final Map<String,PlaceID> mapPlacetoUUID, final Map<String,SourceID> mapEmbeddedSourceToUUID)
	{
		final GedcomLine lineTop = nodeTop.getObject();

//		System.out.print("individual: ");
//		System.out.println(lineTop.getID());

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
					idPersona = new PersonaID(factoryUUID.createUUID());
				}

				final GedcomLine lineEvent = node.getObject();
				final EventType eventtype = EventType.fromGedcom(lineEvent.getTag());
				final Event event = new Event(eventtype,eventtype == EventType.other ? lineEvent.getValue() : "",date,new Surety(0),idPlace,new Surety(0));
				final EventID eventID = new EventID(factoryUUID.createUUID());

				final Role role = new Role(RoleType.actor/*TODO fix role type*/,"",new Surety(0),idPersona,eventID);
				final RoleID roleID = new RoleID(factoryUUID.createUUID());

				
			}
		}
		//Assertion assertion = new Assertion();
	}



	private static UUID pointsToUUID(final TreeNode<GedcomLine> nodePointer, final Map<String,UUID> mapIDtoUUID)
	{
		final GedcomLine line = nodePointer.getObject();
		final UUID uuid = mapIDtoUUID.get(line.getPointer());
		if (uuid == null)
		{
			System.err.println("Cannot find pointed-to node, in line: "+line);
		}
		return uuid;
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


	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
	}
}
