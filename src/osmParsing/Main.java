package osmParsing;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Main 
{
	final static  String FILE_PATH = "/home/akruchinina/git/parseOsm/source/test.osm"; 
	
	static HashMap<String, OsmNode> nodes = new HashMap<>();
	static HashSet<OsmWay> ways = new HashSet<>(); 
	static Integer nodesCount  = 0;
	static Integer edgesCount = 0;

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException
    {
    	double startTime = System.currentTimeMillis();
    	
    	//Парсим node
    	XmlParser.parseNodesFromFile(FILE_PATH, nodes);
   
    	//Парсим way
    	XmlParser.parseWaysFromFile(FILE_PATH, nodes, ways);
    	
		//Добавляем связи между вершинами
    	NodeUtils.setNodeRelation(nodes, ways);
    	
    	//Удаляем изолированные вершины
    	NodeUtils.deleteIsolatedNodes(nodes);
    	
    	//Добавляем короткие id от 0 до N
    	NodeUtils.addShortId(nodes);
    	
    	//Сливаем внутренние вершины
    	NodeUtils.deleteInternalNodes(nodes);

    	//После всех махинаций с ребрами и вершинами - сохраняем итоговое кол-во ребер и вершин
    	setNodesAndEdgesCount();
    	
    	double finishTime = System.currentTimeMillis();
    	
     	
    	//Выводим список ребер
    	System.out.println(String.format("%s %s %s", nodesCount, edgesCount, 1));
    	for (OsmNode node : nodes.values())
    	{
    		for (OsmNode toNode : node.to.values())
    		{
    			System.out.println(node.shortId + " " + toNode.shortId);
    		}
    	} 
    	
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
}
