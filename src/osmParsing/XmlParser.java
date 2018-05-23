package osmParsing;

import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;
import org.w3c.dom.Document;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class XmlParser 
{
	static Document doc;
	private static XmlReader fXmlFile;
	static void parseNodesFromFile(String fileName, HashMap<String, OsmNode> nodes) throws FileNotFoundException, XMLStreamException
	{
		fXmlFile = new XmlReader(fileName);
		HashMap<String, String> nodeAttrsMap = new HashMap<>();
		while (fXmlFile.hasNext())
		{
			nodeAttrsMap = fXmlFile.getNextNode();
			if (nodeAttrsMap.isEmpty())
			{
				break;
			}
			String id = nodeAttrsMap.get("id");
			String lat = nodeAttrsMap.get("lat");
			String lon = nodeAttrsMap.get("lon");
			OsmNode node = new OsmNode(id, lat, lon);
			nodes.put(id, node);
		}
		fXmlFile.close();
	}
	
	static void parseWaysFromFile(String fileName, HashMap<String, OsmNode> nodes, HashSet<OsmWay> ways) throws FileNotFoundException, XMLStreamException
	{
		fXmlFile = new XmlReader(fileName);
		HashMap<String, String> nodeAttrsMap = new HashMap<>();
		while (fXmlFile.hasNext())
		{
			nodeAttrsMap = fXmlFile.getNextWay();
			if (nodeAttrsMap.isEmpty())
			{
				break;
			}
			String id = nodeAttrsMap.get("id");
			String highwayTagValue = nodeAttrsMap.get("highway") != null ? nodeAttrsMap.get("highway") : "" ;
			String oneWayTagValue = nodeAttrsMap.get("oneway") != null ? nodeAttrsMap.get("oneway") : ""; 
			String laneCount = nodeAttrsMap.get("lanes");

			//Берем way только определенных тегов highway    			
			//То есть, проверяем, дорога ли это?
			List<String> allowedHighwayValues = Arrays.asList("motorway", "motorway_link", "trunk", "trunk_link", "primary", "primary_link", "secondary", "secondary_link", "tertiary", "tertiary_link", "unclassified", "road", "residential", "service", "living_street");
			if (!allowedHighwayValues.contains(highwayTagValue))
			{
				continue;
			}
    			 
			//Односторонняя ли?
			boolean isOneWay = false;
			
			//motorway - односторонняя по умолчанию
			if ("yes".equals(oneWayTagValue) || highwayTagValue.startsWith("motorway") && oneWayTagValue.isEmpty())
			{
				isOneWay = true;
			}
			
			//Сколько полос?								
			//Если значеение тега highway подходящее, создаем объект пути
			OsmWay way = new OsmWay(id, isOneWay, laneCount);				
			ways.add(way);   			
    			
			//Добавляем в него вершины
			ArrayList<String> nodeList= fXmlFile.getLastWayNodes();
			for (String nodeId : nodeList) 
			{
				OsmNode node = nodes.get(nodeId);
				if (node != null)
				{
					way.addNode(node);
				}
			}
		}
		fXmlFile.close();
	}	
}
