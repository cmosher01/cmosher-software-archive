package nu.mine.mosher.velocity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class VelocityTest
{
    public static void main(String[] args) throws Throwable
    {
        Context context = new VelocityContext();
        List deps = getDependencies();
        context.put("deps",deps);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out)));
        VelocityWrapper.merge(new File("C:\\Documents and Settings\\chrism\\My Documents\\build_template.vm"), context, writer);
        writer.close();
    }

    public static ArrayList getDependencies() throws ParserConfigurationException, SAXException, IOException
    {
        ArrayList deps = new ArrayList();

        File f = new File("C:\\Documents and Settings\\chrism\\workspace\\metadataImport-1-0\\.classpath");

        DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        DocumentBuilder build = fact.newDocumentBuilder();
        Document doc = build.parse(f);
        NodeList rcpe = doc.getElementsByTagName("classpathentry");
        for (int i = 0; i < rcpe.getLength(); ++i)
        {
            Node cpe = rcpe.item(i);
            NamedNodeMap mapAttrib = cpe.getAttributes();
            Node nodePath = mapAttrib.getNamedItem("path");
            String path = nodePath.getNodeValue();
            if (path.startsWith("/"))
            {
                path = path.substring(1);
                int slash = path.indexOf("/");
                if (slash >= 0)
                {
                    path = path.substring(0,slash);
                }
                int dash = path.indexOf("-");
                String name;
                String version;
                if (dash >= 0)
                {
                    name = path.substring(0, dash);
                    version = path.substring(dash+1);
                }
                else
                {
                    name = path;
                    version = "HEAD";
                }
                Node nodeKind = mapAttrib.getNamedItem("kind");
                String kind = nodeKind.getNodeValue();
                boolean source = kind.equalsIgnoreCase("src");
                Dependency dep = new Dependency(name,version,source);
                if (!deps.contains(dep))
                {
                    deps.add(dep);
                }
            }
        }
        Collections.reverse(deps);
        return deps;
    }
}
