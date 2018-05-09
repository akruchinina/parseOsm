package osmParsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class NodeUtils 
{
	/**
	 * Добавляет вершинам из nodes связи между собой (список смежности) по данным из ways	 
	 * @param nodes
	 * @param ways
	 */
	public static void setNodeRelation(HashMap<String, OsmNode> nodes, HashSet<OsmWay> ways)
    {
    	for (OsmWay way : ways)
    	{
    		ArrayList<OsmNode> wayNodes = way.nodes;
    		int wayNodesSize = wayNodes.size();
    		for (int i = 0; i < wayNodesSize; i++)
    		{
    			OsmNode currentNodeInWay = wayNodes.get(i);
    			if (i != 0)
    			{
        			OsmNode previousNodeInWay = wayNodes.get(i - 1);
        			currentNodeInWay.from.put(previousNodeInWay.id, previousNodeInWay);
    			}
    			if (i != wayNodesSize - 1)
    			{
        			OsmNode nextNodeInWay = wayNodes.get(i + 1);	        			
        			currentNodeInWay.to.put(nextNodeInWay.id, nextNodeInWay);
    			}
    		}
    	}
    }
    
	/**
	 * Удаляет изолированные вершины
	 * то есть те, у которых нет ни входящих ребер, ни исходящих
	 * @param nodes
	 */
    public static void deleteIsolatedNodes(HashMap<String, OsmNode> nodes)
    {
    	HashSet<String> deleteKeys = new HashSet<String>();
    	for (Entry<String, OsmNode> entryNode : nodes.entrySet()) 
    	{
    		if (entryNode.getValue().from.isEmpty() && entryNode.getValue().to.isEmpty())
    		{
    			deleteKeys.add(entryNode.getKey());
    		}
		}
    	
    	for (String key : deleteKeys) 
    	{
    		nodes.remove(key);
		}
    }
    
    /**
     * !!! Верно только для односторонних дорог, нужно править логику!!!
     * 
     * Удаляет соединительные вершины, которые не являются перекрестками
     * (объединяет 2 соседних ребра в одно)
     * @param nodes
     */
    public static void deleteInternalNodes(HashMap<String, OsmNode> nodes)
    {
    	HashSet<String> deleteKeys = new HashSet<String>();
    	for (Entry<String, OsmNode> entryNode : nodes.entrySet()) 
    	{
    		HashMap<String, OsmNode>  fromMap = entryNode.getValue().from;
    		HashMap<String, OsmNode>  toMap = entryNode.getValue().to;
    		if (fromMap.size() == 1 && toMap.size() == 1)
    		{
    			OsmNode from = fromMap.values().iterator().next();
    			OsmNode to = toMap.values().iterator().next();

    			from.to.remove(entryNode.getKey());
    			to.from.remove(entryNode.getKey());
    			
    			from.to.put(to.id, to);
    			to.from.put(from.id, from);
    			deleteKeys.add(entryNode.getKey());
    		}
		}
    	
    	for (String key : deleteKeys) 
    	{
    		nodes.remove(key);
		}
    }
}
