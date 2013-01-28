
package com.seoushigames;


import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.OrderedMap;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SpineJsonToXml {

	static public final String FRAMES_SCALE = "scale";
	static public final String FRAMES_ROTATE = "rotate";
	static public final String FRAMES_TRANSLATE = "translate";
	static public final String FRAMES_ATTACHMENT = "attachment";
	static public final String FRAMES_COLOR = "color";

	static public final String ATTACHMENT_REGION = "region";
	static public final String ATTACHMENT_ANIMATED_REGION = "animatedRegion";

	
	private static final Json json = new Json();
	
	
	public static Boolean convertSkeletonData (String inFileName, String outFileName) 
	{
		//check input file
		FileHandle inFile = new FileHandle(new File(inFileName));
		
		if(!inFile.exists())
		{
			return false;
		}

		//setup output doc
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		
		try 
		{
			docBuilder = docFactory.newDocumentBuilder();
		} 
		catch (ParserConfigurationException e1) 
		{
			return false;
		}
		
		
		//make skeleton node
		Document doc = docBuilder.newDocument();
		Element skelElement = doc.createElement("skeleton");
		doc.appendChild(skelElement);
		
		
		//make bones node
		Element bonesElement = doc.createElement("bones");
		skelElement.appendChild(bonesElement);
		
		OrderedMap<String, OrderedMap> root = json.fromJson(OrderedMap.class, inFile);

		// make all Bone nodes
		OrderedMap<String, OrderedMap> map = root.get("bones");
		for (Entry<String, OrderedMap> entry : map.entries())
		{
			Element boneElement = doc.createElement("bone");
			
			boneElement.setAttribute("name", entry.key);
			boneElement.setAttribute("parent", (String)entry.value.get("parent"));
			boneElement.setAttribute("x", (String)entry.value.get("x"));
			boneElement.setAttribute("y", (String)entry.value.get("y"));
			boneElement.setAttribute("rotation", (String)entry.value.get("rotation"));
			boneElement.setAttribute("scaleX", (String)entry.value.get("scaleX"));
			boneElement.setAttribute("scaleY", (String)entry.value.get("scaleY"));
			
			bonesElement.appendChild(boneElement);
		}

		
		//make slots node
		Element slotsElement = doc.createElement("slots");
		skelElement.appendChild(slotsElement);
		
		// make slot nodes
		map = root.get("slots");
		if (map != null)
		{
			for (Entry<String, OrderedMap> entry : map.entries())
			{
				Element slotElement = doc.createElement("slot");
				
				slotElement.setAttribute("name", entry.key);
				slotElement.setAttribute("bone", (String)entry.value.get("bone"));
				
				String color = (String)entry.value.get("color");
				if (color != null) 
				{
					slotElement.setAttribute("color", color);
				}

				String attachment = (String)entry.value.get("attachment");
				if(attachment != null)
				{
					slotElement.setAttribute("attachment", attachment);
				}
				
				slotsElement.appendChild(slotElement);
			}
		}

		//make skins node
		Element skinsElement = doc.createElement("skins");
		skelElement.appendChild(skinsElement);
				
		// make Skin nodes
		map = root.get("skins");
		if (map != null)
		{
			for (Entry<String, OrderedMap> entry : map.entries()) 
			{
				Element skinElement = doc.createElement("skin");
				skinElement.setAttribute("name", entry.key);
				
				
				for (Entry<String, OrderedMap> slotEntry : ((OrderedMap<String, OrderedMap>)entry.value).entries()) 
				{
					Element slotElement = doc.createElement("slot");
					slotElement.setAttribute("name", slotEntry.key);
					
					for (Entry<String, OrderedMap> attachmentEntry : ((OrderedMap<String, OrderedMap>)slotEntry.value).entries()) 
					{
						Element attachmentElement = doc.createElement("attachment");
						attachmentElement.setAttribute("name", attachmentEntry.key);
						
						OrderedMap attachValuesEntry = attachmentEntry.value;
						
						String x = (String)attachValuesEntry.get("x");
						if(x != null)
						{
							attachmentElement.setAttribute("x", x);
						}
						
						String y = (String)attachValuesEntry.get("y");
						if(y != null)
						{
							attachmentElement.setAttribute("y", y);
						}
						
						String rotation = (String)attachValuesEntry.get("rotation");
						if(rotation != null)
						{
							attachmentElement.setAttribute("rotation", y);
						}
						
						String scaleX = (String)attachValuesEntry.get("scaleX");
						if(scaleX != null)
						{
							attachmentElement.setAttribute("scaleX", scaleX);
						}
						
						String scaleY = (String)attachValuesEntry.get("scaleY");
						if(scaleY != null)
						{
							attachmentElement.setAttribute("scaleY", scaleY);
						}
						
						slotElement.appendChild(attachmentElement);
					}
					
					skinElement.appendChild(slotElement);
				}
				
				skinsElement.appendChild(skinElement);
			}
		}
		
		try
		{
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(outFileName));
			
			transformer.transform(source, result);
		}
		catch(Exception e)
		{
			return false;
		}
		
		return true;
	}
	
	
	public static Boolean convertAnimationData(String inFileName, String outFileName)
	{
		//check input file
		FileHandle inFile = new FileHandle(new File(inFileName));
		
		if(!inFile.exists())
		{
			return false;
		}

		//setup output doc
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		
		try 
		{
			docBuilder = docFactory.newDocumentBuilder();
		} 
		catch (ParserConfigurationException e1) 
		{
			return false;
		}
		
		
		OrderedMap<String, ?> map = json.fromJson(OrderedMap.class, inFile);
		
		//make animation node
		Document doc = docBuilder.newDocument();
		Element animElement = doc.createElement("animation");
		doc.appendChild(animElement);
		
		
		//make bones node
		Element bonesElement = doc.createElement("bones");
		animElement.appendChild(bonesElement);

		// make all bone nodes
		OrderedMap<String, ?> bonesMap = (OrderedMap)map.get("bones");
		for (Entry<String, ?> entry : bonesMap.entries()) 
		{
			Element boneElement = doc.createElement("bone");
			
			boneElement.setAttribute("name", entry.key);
			
			// parse properties
			OrderedMap<?, ?> propertyMap = (OrderedMap)entry.value;
			for (Entry propertyEntry : propertyMap.entries())
			{
				Element propsElement = doc.createElement("properties");
				propsElement.setAttribute("name", (String)propertyEntry.key);
				
				OrderedMap<?, ?> frameMap = (OrderedMap)propertyEntry.value;
				String framesType = (String)propertyEntry.key;
				
				// parse rotations
				if (framesType.equals(FRAMES_ROTATE)) 
				{
					for (Entry frameEntry : frameMap.entries()) 
					{
						Element frameElement = doc.createElement("frame");
						frameElement.setAttribute("time", (String)frameEntry.key);
						
						
						OrderedMap valueMap = (OrderedMap)frameEntry.value;
						frameElement.setAttribute("angle", (String)valueMap.get("angle"));
						
						//read curves
						Object curveObject = valueMap.get("curve");
						if (curveObject != null)
						{
							if(curveObject.equals("stepped"))
							{
								frameElement.setAttribute("curve", "stepped");
							}
							else if (curveObject instanceof Array) 
							{
								Array curve = (Array)curveObject;
								frameElement.setAttribute("curve", curve.get(0) + "," + curve.get(1) + "," + curve.get(2) + "," + curve.get(3));
							}
						}
						
						propsElement.appendChild(frameElement);
					}
				} 
				else if (framesType.equals(FRAMES_TRANSLATE) || framesType.equals(FRAMES_SCALE)) 
				{
					for (Entry frameEntry : frameMap.entries()) 
					{
						Element frameElement = doc.createElement("frame");
						frameElement.setAttribute("time", (String)frameEntry.key);
						
						OrderedMap valueMap = (OrderedMap)frameEntry.value;
						frameElement.setAttribute("x", (String)valueMap.get("x"));
						frameElement.setAttribute("y", (String)valueMap.get("y"));
						
						//read curves
						Object curveObject = valueMap.get("curve");
						if (curveObject != null)
						{
							if(curveObject.equals("stepped"))
							{
								frameElement.setAttribute("curve", "stepped");
							}
							else if (curveObject instanceof Array) 
							{
								Array curve = (Array)curveObject;
								frameElement.setAttribute("curve", curve.get(0) + "," + curve.get(1) + "," + curve.get(2) + "," + curve.get(3));
							}
						}
						
						propsElement.appendChild(frameElement);
					}
				} 
				else
				{
					return false;
				}
				
				boneElement.appendChild(propsElement);
			}
			
			bonesElement.appendChild(boneElement);
		}
		
		try
		{
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(outFileName));
			
			transformer.transform(source, result);
		}
		catch(Exception e)
		{
			return false;
		}
				
		return true;
	}
	
}
