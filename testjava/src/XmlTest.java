import java.io.File;
import java.io.IOException;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlTest
{
	public XmlTest()
	{
	}

	public static void main(String[] args) throws IOException, SAXException
	{
		SAXParser par = new SAXParser();
		par.setErrorHandler(new ErrorHandler()
		{
			public void warning(SAXParseException e) throws SAXParseException { throw e; }
			public void error(SAXParseException e) throws SAXParseException { throw e; }
			public void fatalError(SAXParseException e) throws SAXParseException { throw e; }
		});
		par.setFeature("http://xml.org/sax/features/validation",true);
		par.setFeature("http://apache.org/xml/features/validation/schema",true);
		par.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation","test.xsd");
		par.parse(new InputSource(new File(args[0]).getPath()));
	}
}
