package osmParsing;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;


import org.xml.sax.SAXException;

public class Main 
{
	final static String SOURCE_DIR = "/home/akruchinina/git/parseOsm/source/";
	final static String OUTPUT_DIR = "/home/akruchinina/git/parseOsm/output/";
	final static String CITY = "ekb";
	final static  String FILE_PATH = SOURCE_DIR + CITY + ".osm"; 
	
	static HashMap<String, OsmNode> nodes = new HashMap<>();
	static HashSet<OsmWay> ways = new HashSet<>(); 
	static Integer nodesCount  = 0;
	static Integer edgesCount = 0;
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XMLStreamException, FactoryConfigurationError
    {    	
    	double startTime = System.currentTimeMillis();
    	
    	System.out.println("1");
    	//Парсим node
    	XmlParser.parseNodesFromFile(FILE_PATH, nodes);

    	System.out.println("2");
    	//Парсим way
    	XmlParser.parseWaysFromFile(FILE_PATH, nodes, ways);
    	
    	System.out.println("3");
		//Добавляем связи между вершинами
    	NodeUtils.setNodeRelation(nodes, ways);
    	
    	System.out.println("4");
    	//Удаляем изолированные вершины
    	NodeUtils.deleteIsolatedNodes(nodes);
    	
    	System.out.println("5");
    	//Сливаем внутренние вершины
    	NodeUtils.deleteInternalNodes(nodes);
    	
    	System.out.println("6");
    	//Добавляем короткие id от 0 до N
    	NodeUtils.addShortId(nodes);

    	//После всех махинаций с ребрами и вершинами - сохраняем итоговое кол-во ребер и вершин
    	setNodesAndEdgesCount();
    	
    	double finishTime = System.currentTimeMillis();
    	
     	
    	//Выводим список ребер
    	printEdgeListWithDistance(CITY);
    	printEdgeListWithCapacity(CITY);
    	
    	//Выводим время работы
    	System.out.println("TIME: " + (finishTime - startTime));
    }
	
	private static void setNodesAndEdgesCount() 
	{
		nodesCount = nodes.size();
		for (OsmNode entryNode : nodes.values())
		{
			edgesCount += entryNode.to.size();
		}
	}
	
	private static void printEdgeListWithDistance(String cityPrefix)
	{
		try(FileWriter writer = new FileWriter(OUTPUT_DIR + cityPrefix + "Distance.txt", false))
        {
			writer.write(String.format("%s %s %s\n", nodesCount, edgesCount, 1));
	    	for (OsmNode node : nodes.values())
	    	{
	    		for (OsmNode toNode : node.to.values())
	    		{
	    			writer.write(String.format("%s %s %(.2f\n", node.shortId, toNode.shortId, NodeUtils.calcDistanceInKilometr(node, toNode)));
	    		}
	    	} 
	    	writer.flush();   
        }
        catch(IOException ex)
		{     
            System.out.println(ex.getMessage());
        } 
	}
	
	private static void printEdgeListWithCapacity(String cityPrefix)
	{
		try(FileWriter writer = new FileWriter(OUTPUT_DIR + cityPrefix + "Capacity.txt", false))
        {

			writer.write(String.format("%s %s %s\n", nodesCount, edgesCount, 1));
	    	for (OsmNode node : nodes.values())
	    	{
	    		for (OsmNode toNode : node.to.values())
	    		{
	    			writer.write(String.format("%s %s %s\n", node.id, toNode.id, node.toCapacity.get((toNode.id))));
	    		}
	    	} 
	    	writer.flush();   
        }
        catch(IOException ex)
		{     
            System.out.println(ex.getMessage());
        } 
	}
}
