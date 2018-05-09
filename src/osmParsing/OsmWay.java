package osmParsing;

import java.util.ArrayList;

public class OsmWay 
{
	String id;
	ArrayList<OsmNode> nodes;
	
	public OsmWay(String id)
	{
		this.id = id;
		this.nodes = new ArrayList<>();
	}
	
	public void addNode(OsmNode node)
	{
		this.nodes.add(node);
	}
}
