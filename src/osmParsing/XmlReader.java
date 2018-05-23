package osmParsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public class XmlReader implements AutoCloseable 
{
    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();

    private XMLStreamReader reader;    
    private ArrayList<String> lastWayNodes; 

    public XmlReader(String filePath) throws XMLStreamException, FileNotFoundException 
    {
    	File fXmlFile = new File(filePath);
		InputStream is = new FileInputStream(fXmlFile);
		this.reader = FACTORY.createXMLStreamReader(is);
		this.lastWayNodes = new ArrayList<>();
    }
    
    private void removeLastWayNodes()
    {
    	this.lastWayNodes = new ArrayList<>();
    }

   
    public ArrayList<String> getLastWayNodes()
    {
    	return lastWayNodes;
    }
    
    
    public XMLStreamReader getReader() 
    {
       return reader;
    }

    @Override
    public void close() 
    {
       if (reader != null) 
       {
          try 
          {
             reader.close();
          } 
          catch (XMLStreamException e) 
          { 
        	  // empty
          }
       }
    }
    

    public HashMap<String, String> getNextNode()
    {
    	HashMap<String, String> nodeAttrsMap = new HashMap<>();
    	int event;
		try 
		{
			while (reader.hasNext()) 
			{
				event = reader.next();
			    if (event == XMLEvent.START_ELEMENT && "node".equals(reader.getLocalName())) 
			    {
			    	int attrCount = reader.getAttributeCount();
			    	for (int i = 0; i < attrCount; i++)
			    	{
			    		String name = reader.getAttributeName(i).toString();
			    		switch (name) 
			    		{
			    			case "id":
			        	    	nodeAttrsMap.put("id", reader.getAttributeValue(i));
			    				break;
		
			    			case "lat":
			        	    	nodeAttrsMap.put("lat", reader.getAttributeValue(i));
			    				break;
			    				
			    			case "lon":
			        	    	nodeAttrsMap.put("lon", reader.getAttributeValue(i));
			    				break;
			    			default:
			    				break;
						}
			    	}
			    	return nodeAttrsMap;
			    }
		    }
		} 
		catch (XMLStreamException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nodeAttrsMap;
    }
    
    public HashMap<String, String> getNextWay()
    {
    	HashMap<String, String> wayAttrsMap = new HashMap<>();
    	try 
    	{
			while (reader.hasNext()) 
			{
				int event = reader.next();
			    if (event == XMLEvent.START_ELEMENT && "way".equals(reader.getLocalName())) 
			    {
				   	removeLastWayNodes();
			    	wayAttrsMap.put("id", reader.getAttributeValue(0));
			    	
				   	while (reader.hasNext() && !(event == XMLEvent.END_ELEMENT && "way".equals(reader.getLocalName())))
			    	{
				   		event = reader.next();
			    	   	if (event == XMLEvent.START_ELEMENT && "tag".equals(reader.getLocalName())) 
			    	   	{
			    	   		String k = reader.getAttributeValue(0).toString();
			    	   		String v = reader.getAttributeValue(1).toString();
			   	    	   	if (k.equals("highway") || k.equals("lanes") || k.equals("oneway"))
			   	    	   	{
			   	    	   		wayAttrsMap.put(k, v);
			   	    	   	}
			    	   		
			    	   	}
			    	   	
			    		if (event == XMLEvent.START_ELEMENT && "nd".equals(reader.getLocalName())) 
			    	   	{
			    	   		String nodeId = reader.getAttributeValue(0).toString();
			    	   		lastWayNodes.add(nodeId);			    	   		
			    	   	}
			    	   	
			    	}
				   	return wayAttrsMap;
			    }
			}
		} 
    	catch (XMLStreamException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wayAttrsMap;
    }
    
    public boolean hasNext()
    {
    	try 
    	{
    		return reader.hasNext();
		} 
    	catch (XMLStreamException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
    }
}