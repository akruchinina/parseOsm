package osmParsing;

import java.util.HashMap;

public class OsmNode 
{
	String id;
	String lat;
	String lon;
	HashMap<String, OsmNode> from;
	HashMap<String, OsmNode> to;
	
	public OsmNode(String id, String lat, String lon)
	{
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		this.from = new HashMap<>();
		this.to = new HashMap<>();
	}
	
	public String toString()
	{
		return String.format("id: %s", this.id);
	}	
}
