package osmParsing;

import java.util.ArrayList;

public class OsmWay 
{
	String id;
	ArrayList<OsmNode> nodes;
	Boolean isOneWay;
	Integer laneCount;
	
	public OsmWay(String id, Boolean isOneWay, String lanesCount)
	{
		this.id = id;
		this.nodes = new ArrayList<>();
		this.isOneWay = isOneWay;
		this.laneCount = null != lanesCount ? Integer.valueOf(lanesCount) : 1;
	}
	
	public void addNode(OsmNode node)
	{
		this.nodes.add(node);
	}
}
