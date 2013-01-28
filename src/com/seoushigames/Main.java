package com.seoushigames;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class Main {

	static void scanDirTree(File file, Collection<File> all) 
	{
	    File[] children = file.listFiles();
	    
	    if (children != null)
	    {
	        for(File child : children) 
	        {
	        	if(child.getName().contains(".json"))
	        	{
	        		all.add(child);
	        	}
	            scanDirTree(child, all);
	        }
	    }
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		Collection<File> files = new ArrayList<File>();
		scanDirTree(new File("."), files);
		
		
	    for(File f : files)
	    {
	    	if(f.getName().contains("skeleton"))
	    	{
	    		String name = f.getPath();
	    		String outName = name.substring(0, name.length() - 4) + "xml";
	    		
	    		if(!SpineJsonToXml.convertSkeletonData(name, outName))
	    		{
	    			System.out.println("'" + name + "' [FAILED]");
	    		}
	    		else
	    		{
	    			System.out.println("'" + name + "' [OK]");
	    		}
	    	}
	    	else if(f.getName().contains("animation"))
	    	{
	    		String name = f.getPath();
	    		String outName = name.substring(0, name.length() - 4) + "xml";
	    		
	    		if(!SpineJsonToXml.convertAnimationData(name, outName))
	    		{
	    			System.out.println("'" + name + "' [FAILED]");
	    		}
	    		else
	    		{
	    			System.out.println("'" + name + "' [OK]");
	    		}
	    	}
	    }
	}

}
