/*
 * Created on Nov 21, 2005
 */
package nu.mine.mosher.charsets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AnselDoc
{
	private static final Map<Integer,Integer> mapAnselToUnicode;
	static
	{
		try
		{
			final Map<Integer,Integer> map = new HashMap<Integer,Integer>();
			init(map);
			mapAnselToUnicode = Collections.<Integer,Integer>unmodifiableMap(map);
		}
		catch (final Throwable e)
		{
			throw new IllegalStateException(e);
		}
	}

	private static void init(final Map<Integer,Integer> map) throws ParserConfigurationException, SAXException, IOException
	{
		final DocumentBuilderFactory factoryBuilderDocument = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builderDocument = factoryBuilderDocument.newDocumentBuilder();
		
		final InputStream anselxml = AnselDoc.class.getResourceAsStream("ansel.xml");
		if (anselxml == null)
		{
			throw new IllegalStateException("cannot find ansel.xml");
		}

		final Document document = builderDocument.parse(anselxml);
		final NodeList nodelist = document.getChildNodes();
		for (int iNode = 0; iNode < nodelist.getLength(); ++iNode)
		{
			final Node node = nodelist.item(iNode);
			if (!node.getNodeName().equalsIgnoreCase("characterSet"))
			{
				throw new IllegalStateException("top level element in ansel.xml must be characterSet");
			}
			final NodeList nodelistCode = node.getChildNodes();
			for (int iNodeCode = 0; iNodeCode < nodelistCode.getLength(); ++iNodeCode)
			{
				final Node nodeCode = nodelistCode.item(iNodeCode);
				if (nodeCode.getNodeName().equalsIgnoreCase("code"))
				{
					int marc = 0;
					int ucs = 0;

					final NodeList nodelistMapping = nodeCode.getChildNodes();
					for (int iMapping = 0; iMapping < nodelistMapping.getLength(); ++iMapping)
					{
						final Node nodeMapping = nodelistMapping.item(iMapping);
						if (nodeMapping.getNodeName().equalsIgnoreCase("marc"))
						{
							try
							{
								if (nodeMapping.getTextContent().length() > 0)
								{
									marc = Integer.parseInt(nodeMapping.getTextContent(),16);
								}
							}
							catch (final Throwable e)
							{
								throw new IllegalStateException(e);
							}
						}
						else if (nodeMapping.getNodeName().equalsIgnoreCase("ucs"))
						{
							try
							{
								if (nodeMapping.getTextContent().length() > 0)
								{
									ucs = Integer.parseInt(nodeMapping.getTextContent(),16);
								}
							}
							catch (final Throwable e)
							{
								throw new IllegalStateException(e);
							}
						}
					}
					if (map.containsKey(marc))
					{
						throw new IllegalStateException("duplicate ANSEL character");
					}
					map.put(marc,ucs);
				}
			}
		}
	}

	public static Map<Integer,Integer> map()
	{
		return AnselDoc.mapAnselToUnicode;
	}
}
