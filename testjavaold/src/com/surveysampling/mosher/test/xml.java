package com.surveysampling.mosher.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;

class xml_remnant
{   public void printNode(int level, Node node)
    {
		String s = null;
    	if (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.CDATA_SECTION_NODE)
    		s = node.getNodeValue();
	    else
	        s = node.getNodeName();
//	    if (s.equals("template"))
//	    {
//	    	Node nodeQuery = getOneChildNamed(node,"query");
//	    	ArrayList r = getChildrenNamed(node,"query");
//	    	for (Iterator i = r.iterator(); i.hasNext(); )
//	    	   	System.out.println(((Node)i.next()).getNodeName());
//		}
		outputLine(level,s);
    	++level;
    	for (Iterator i = childNodes(node).iterator(); i.hasNext(); )
        	printNode(level,(Node)i.next());
    }
    private List childNodes(Node node)
    {
        Node n = node; node = n;
        return new ArrayList();
    }
    
    
    private void outputLine(int level, String s)
    {
    	if (s.trim().length() == 0)
    		return;

		for (int i = 0; i < level; ++i)
			System.out.print("....");

		System.out.print("\"");
		System.out.print(s.trim());
		System.out.println("\"");
    }
}
