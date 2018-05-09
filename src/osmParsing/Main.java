package osmParsing;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Main 
{
	final static  String FILE_PATH = "/home/akruchinina/git/parseOsm/source/testEkb.osm"; 
	
	static HashMap<String, OsmNode> nodes = new HashMap<>();
	static HashSet<OsmWay> ways = new HashSet<>(); 
	

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

    	//Сливаем внутренние вершины
    	NodeUtils.deleteInternalNodes(nodes);
    	
    	double finishTime = System.currentTimeMillis();
    	
    	//Выводим список смежности
    	System.out.println("Nodes:");
    	for (OsmNode node : nodes.values())
    	{
            System.out.println(node.id + " " + node.from.toString() + " " + node.to.toString());
    	} 
    	
    	//Выводим время работы
    	System.out.println("TIME: " + (finishTime - startTime));
    }
}
