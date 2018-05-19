package osmParsing;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class XmlParser 
{
	static void parseNodesFromFile(String fileName, HashMap<String, OsmNode> nodes)
	{
   		Document doc = getXmlDocument(fileName);	
    	NodeList nodeList = doc.getElementsByTagName("node");  
    	int nodeListSize = nodeList.getLength();
    	for (int i = 0; i < nodeListSize; i++) 
    	{
    		Node currentNode = nodeList.item(i);    				
    		if (currentNode.getNodeType() == Node.ELEMENT_NODE) 
    		{
    			Element eElement = (Element) currentNode;
    			String id = eElement.getAttribute("id");
    			String lat = eElement.getAttribute("lat");
    			String lon = eElement.getAttribute("lon");
    			
    			OsmNode node = new OsmNode(id, lat, lon);
    			nodes.put(id, node);
    		}
    	}
	}
	
	static void parseWaysFromFile(String fileName, HashMap<String, OsmNode> nodes, HashSet<OsmWay> ways)
	{
		Document doc = getXmlDocument(fileName);	
    	NodeList wayList = doc.getElementsByTagName("way");   
    	int wayListSize = wayList.getLength();
    	for (int i = 0; i < wayListSize; i++) 
    	{
    		Node currentWay = wayList.item(i);
    				
    		if (currentWay.getNodeType() == Node.ELEMENT_NODE) 
    		{
    			Element eElement = (Element) currentWay;
    			String id = eElement.getAttribute("id");
    			NodeList tagList = eElement.getElementsByTagName("tag"); 
    			
    			//Берем way только определенных тегов highway
    			List<String> allowedHighwayValues = Arrays.asList("motorway", "motorway_link", "trunk", "trunk_link", "primary", "primary_link", "secondary", "secondary_link", "tertiary", "tertiary_link", "unclassified", "road", "residential", "service", "living_street");
    			
    			//Проверяем, дорога ли это?
    			String highwayTagValue = getTagValue(tagList, "highway");
    	
				if (!allowedHighwayValues.contains(highwayTagValue))
				{
					continue;
				}
    			 
				//Односторонняя ли?
				boolean isOneWay = false;
				String oneWayTagValue = getTagValue(tagList, "oneway");
				//motorway - односторонняя по умолчанию
				if ("yes".equals(oneWayTagValue) || highwayTagValue.startsWith("motorway") && oneWayTagValue.isEmpty())
				{
					isOneWay = true;
				}
				
				//Если значеение тега highway подходящее, создаем объект пути
				OsmWay way = new OsmWay(id, isOneWay);				
				ways.add(way);   			
    			
				//Добавляем в него вершины
				NodeList nodeList = eElement.getElementsByTagName("nd");    	
    			int nodeListSize = nodeList.getLength();
				for (int j = 0; j < nodeListSize; j++)
				{
					Node currentWayNode = nodeList.item(j);
					OsmNode node = nodes.get(((Element) currentWayNode).getAttribute("ref"));
					//Добавляем только те, что есть у нас в мапе вершин
					if (node != null)
					{
						way.addNode(node);
					}
				}
    		}
    	}
	}
	/**
	 * Создает xml-документ для парсинга
	 */
	private static Document getXmlDocument(String fileName)
	{
		File fXmlFile = new File(fileName);
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder = null;
    	Document doc = null;
    	try 
		{
			dBuilder = dbFactory.newDocumentBuilder(); 
			doc = dBuilder.parse(fXmlFile);
		}
		catch (ParserConfigurationException | SAXException | IOException e) 
		{
			e.printStackTrace();
		}
    	doc.getDocumentElement().normalize();    	
		return doc;
	}
	
	private static String getTagValue(NodeList tagList, String tagKey)
	{
		int tagListSize = tagList.getLength(); 
		for (int j = 0; j < tagListSize; j++)
		{
			Element tagElement = (Element) tagList.item(j);
			String key = tagElement.getAttribute("k");
			String value = tagElement.getAttribute("v");
			if (key.equals(tagKey))
			{
				return value;
			}
		}
		return null;
	}
	
}
