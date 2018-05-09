package osmParsing;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Main 
{
	final static  String FILE_PATH = "/home/akruchinina/workspace/osmParsing/source/test.osm"; 
	
	static HashMap<String, OsmNode> nodes = new HashMap<>();
	static HashSet<OsmWay> ways = new HashSet<>(); 
	

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException
    {
    	XmlParser.parseNodesFromFile(FILE_PATH, nodes);
    	XmlParser.parseWaysFromFile(FILE_PATH, nodes, ways);
    	    	
    	System.out.println("Nodes:");
    	for (OsmNode node : nodes.values())
    	{
            System.out.println(node.id + " " + node.lat + " " + node.lon);
    	} 
    	
    	System.out.println("Ways:");
    	for (OsmWay way : ways)
    	{
            System.out.println(way.id + " " + way.nodes.toString());
    	}

    }	    
}
