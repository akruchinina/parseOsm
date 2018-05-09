package osmParsing;

public class OsmNode 
{
	String id;
	String lat;
	String lon;
	
	public OsmNode(String id, String lat, String lon)
	{
		this.id = id;
		this.lat = lat;
		this.lon = lon;		
	}
	
	public String toString()
	{
		return String.format("id: %s", this.id);
	}	
}
