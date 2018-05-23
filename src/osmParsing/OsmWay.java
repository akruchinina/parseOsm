package osmParsing;

import java.util.ArrayList;

public class OsmWay 
{
	String id;
	ArrayList<OsmNode> nodes;
	Boolean isOneWay;
	Integer capacity;
	Integer reverseCapacity;
	
	private String DELIMETER = ";";
	
	public OsmWay(String id, Boolean isOneWay, String lanesCount)
	{
		this.id = id;
		this.nodes = new ArrayList<>();
		this.isOneWay = isOneWay;
		
		if (null != lanesCount && !lanesCount.isEmpty())
		{
			//Если кол-во полос в разные стороны не одинаковое
			if (lanesCount.contains(DELIMETER))
			{
				this.capacity = Integer.valueOf(lanesCount.split(DELIMETER)[0]) * 1000;
				this.reverseCapacity = Integer.valueOf(lanesCount.split(DELIMETER)[1]) * 1000;
			}
			else
			{
				this.capacity = isOneWay? Integer.valueOf(lanesCount) * 1000 : Integer.valueOf(lanesCount) * 1000 / 2;
				this.reverseCapacity = this.capacity;
			}
		}
		else
		{
			//по умолчанию - двухполосная дорога
			this.capacity = isOneWay ? 1000 : 2000;
			this.reverseCapacity = this.capacity;
		}
	}
	
	public void addNode(OsmNode node)
	{
		this.nodes.add(node);
	}
}
