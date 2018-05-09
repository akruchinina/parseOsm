package osmParsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Main 
{
	final static  String FILE_PATH = "/home/akruchinina/git/parseOsm/source/testEkb.osm"; 
	
	static HashMap<String, OsmNode> nodes = new HashMap<>();
	static HashSet<OsmWay> ways = new HashSet<>(); 
	

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException
    {
    	double startTime = System.currentTimeMillis();
    	System.out.println(1);
    	XmlParser.parseNodesFromFile(FILE_PATH, nodes);
    	System.out.println(2);
    	XmlParser.parseWaysFromFile(FILE_PATH, nodes, ways);
    	System.out.println(3);
    	setNodeRelation(nodes, ways);
    	System.out.println(4);
    	deleteIsolatedNodes(nodes);
    	System.out.println(5);
    	mergeInternalNodes(nodes);
    	
    	double finishTime = System.currentTimeMillis();
    	
    	System.out.println("Nodes:");
    	for (OsmNode node : nodes.values())
    	{
            System.out.println(node.id + " " + node.from.toString() + " " + node.to.toString());
    	} 
    	
    	System.out.println("TIME: " + (finishTime - startTime));
  /*  	System.out.println("Ways:");
    	for (OsmWay way : ways)
    	{
            System.out.println(way.id + " " + way.nodes.toString());
    	}*/
    }
    
    public static void setNodeRelation(HashMap<String, OsmNode> nodes, HashSet<OsmWay> ways)
    {
    	for (OsmWay way : ways)
    	{
    		ArrayList<OsmNode> wayNodes = way.nodes;
    		int wayNodesSize = wayNodes.size();
    		for (int i = 0; i < wayNodesSize; i++)
    		{
    			OsmNode currentNodeInWay = wayNodes.get(i);
    			if (i != 0)
    			{
        			OsmNode previousNodeInWay = wayNodes.get(i - 1);
        			currentNodeInWay.from.put(previousNodeInWay.id, previousNodeInWay);
    			}
    			if (i != wayNodesSize - 1)
    			{
        			OsmNode nextNodeInWay = wayNodes.get(i + 1);	        			
        			currentNodeInWay.to.put(nextNodeInWay.id, nextNodeInWay);
    			}
    		}
    	}
    }
    
    public static void deleteIsolatedNodes(HashMap<String, OsmNode> nodes)
    {
    	HashSet<String> deleteKeys = new HashSet<String>();
    	for (Entry<String, OsmNode> entryNode : nodes.entrySet()) 
    	{
    		if (entryNode.getValue().from.isEmpty() && entryNode.getValue().to.isEmpty())
    		{
    			deleteKeys.add(entryNode.getKey());
    		}
		}
    	
    	for (String key : deleteKeys) 
    	{
    		nodes.remove(key);
		}
    }
    
    public static void mergeInternalNodes(HashMap<String, OsmNode> nodes)
    {
    	HashSet<String> deleteKeys = new HashSet<String>();
    	for (Entry<String, OsmNode> entryNode : nodes.entrySet()) 
    	{
    		HashMap<String, OsmNode>  fromMap = entryNode.getValue().from;
    		HashMap<String, OsmNode>  toMap = entryNode.getValue().to;
    		if (fromMap.size() == 1 && toMap.size() == 1)
    		{
    			OsmNode from = fromMap.values().iterator().next();
    			OsmNode to = toMap.values().iterator().next();

    			from.to.remove(entryNode.getKey());
    			to.from.remove(entryNode.getKey());
    			
    			from.to.put(to.id, to);
    			to.from.put(from.id, from);
    			deleteKeys.add(entryNode.getKey());
    		}
		}
    	
    	for (String key : deleteKeys) 
    	{
    		nodes.remove(key);
		}
    }
}
