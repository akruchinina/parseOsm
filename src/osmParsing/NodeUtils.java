package osmParsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class NodeUtils 
{
	public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
			
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
    			Integer lineCount = way.isOneWay ? way.laneCount * 1000 :  way.laneCount * 1000 / 2;
    			OsmNode currentNodeInWay = wayNodes.get(i);
    			if (i != 0)
    			{
        			OsmNode previousNodeInWay = wayNodes.get(i - 1);
        			currentNodeInWay.from.put(previousNodeInWay.id, previousNodeInWay);
          			        			
        			if (!way.isOneWay)
        			{
            			currentNodeInWay.to.put(previousNodeInWay.id, previousNodeInWay);
            			currentNodeInWay.toCapacity.put(previousNodeInWay.id, lineCount);
        			}
    			}
    			if (i != wayNodesSize - 1)
    			{
        			OsmNode nextNodeInWay = wayNodes.get(i + 1);	        			
        			currentNodeInWay.to.put(nextNodeInWay.id, nextNodeInWay);
        			currentNodeInWay.toCapacity.put(nextNodeInWay.id, lineCount); 
        			
        			if (!way.isOneWay)
        			{
        				currentNodeInWay.from.put(nextNodeInWay.id, nextNodeInWay);
        			}
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
     * Удаляет соединительные вершины, которые не являются перекрестками
     * 
     * @param nodes
     */
    public static void deleteInternalNodes(HashMap<String, OsmNode> nodes)
    {
    	HashSet<String> deleteKeys = new HashSet<String>();
    	for (Entry<String, OsmNode> entryNode : nodes.entrySet()) 
    	{
    		HashMap<String, OsmNode>  fromMap = entryNode.getValue().from;
    		HashMap<String, OsmNode>  toMap = entryNode.getValue().to;
    		if (fromMap.size() == 1 && toMap.size() == 1  && !fromMap.equals(toMap))
    		{
    			OsmNode from = fromMap.values().iterator().next();
    			OsmNode to = toMap.values().iterator().next();

    			Integer capacity = from.toCapacity.get(entryNode.getKey());
    			from.to.remove(entryNode.getKey());
    			to.from.remove(entryNode.getKey());
    			
    			from.to.put(to.id, to);
    			from.toCapacity.put(to.id, capacity);
    			
    			to.from.put(from.id, from);
    			deleteKeys.add(entryNode.getKey());
    		}
    		
    		if (fromMap.size() == 2 && toMap.size() == 2  && fromMap.equals(toMap))
    		{
    			Iterator<OsmNode> iterator = fromMap.values().iterator();
    			OsmNode firstNode = iterator.next();
    			OsmNode secondNode = iterator.next();
    			
    			
    			firstNode.to.remove(entryNode.getKey());
    			firstNode.from.remove(entryNode.getKey());

    			secondNode.to.remove(entryNode.getKey());
    			secondNode.from.remove(entryNode.getKey());
    			
    			firstNode.to.put(secondNode.id, secondNode);
    			
    			firstNode.from.put(secondNode.id, secondNode);

    			secondNode.to.put(firstNode.id, firstNode);
    			secondNode.from.put(firstNode.id, firstNode);

    			Integer capacity = firstNode.toCapacity.get(entryNode.getKey());
    			firstNode.toCapacity.put(secondNode.id, capacity);
    			secondNode.toCapacity.put(firstNode.id, capacity);
    			
    			deleteKeys.add(entryNode.getKey());
    		}
		}
    	
    	for (String key : deleteKeys) 
    	{
    		nodes.remove(key);
		}
    }

	public static void addShortId(HashMap<String, OsmNode> nodes) 
	{
		int id = 1;
		for (Entry<String, OsmNode> entryNode : nodes.entrySet()) 
		{
			entryNode.getValue().setShortId(id++);
		}
	}

	public static Double calcDistanceInKilometr(OsmNode node, OsmNode toNode) 
	{
		double aLat = Double.valueOf(node.lat);
		double aLng = Double.valueOf(node.lon);
		double bLat = Double.valueOf(toNode.lat);
		double bLng = Double.valueOf(toNode.lon);

	    double latDistance = Math.toRadians(aLat - bLat);
	    double lngDistance = Math.toRadians(aLng - bLng);

	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	      + Math.cos(Math.toRadians(aLat)) * Math.cos(Math.toRadians(bLat))
	      * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

	    return AVERAGE_RADIUS_OF_EARTH_KM * c;
	}
}
