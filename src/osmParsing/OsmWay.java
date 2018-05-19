package osmParsing;

import java.util.ArrayList;

public class OsmWay 
{
	String id;
	ArrayList<OsmNode> nodes;
	Boolean isOneWay;
	
	public OsmWay(String id, Boolean isOneWay)
	{
		this.id = id;
		this.nodes = new ArrayList<>();
		this.isOneWay = isOneWay;
	}
	
	public void addNode(OsmNode node)
	{
		this.nodes.add(node);
	}
}
