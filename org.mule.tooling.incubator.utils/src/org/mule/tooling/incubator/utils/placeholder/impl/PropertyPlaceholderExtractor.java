package org.mule.tooling.incubator.utils.placeholder.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PropertyPlaceholderExtractor {
	
	private final String fileContents;
	
    private static final String PLACEHOLDER_START = "${";
    private static final String PLACEHOLDER_END = "}";
    
	public PropertyPlaceholderExtractor(String fileContents) {
		this.fileContents = fileContents;
	}
	
	public Set<String> extractFileKeys(HashMap<String, String> defaultValuesCache) {
		
		int currentPosition = 0;
		
		Set<String> keys = new HashSet<String>();
		
	    //perform the search
	    while(currentPosition < fileContents.length()) {
	
	
	        currentPosition = fileContents.indexOf(PLACEHOLDER_START, currentPosition);
	
	        if (currentPosition < 0) {
	            return keys;
	        }
	
	        int closePosition = fileContents.indexOf(PLACEHOLDER_END, currentPosition);
	
	        if (closePosition < 0) {
	            throw new IllegalStateException("File is not properly formed.");
	        }
	
	        String key = fileContents.substring(currentPosition + PLACEHOLDER_START.length(), closePosition);
	        
	        keys.add(processKey(key, defaultValuesCache));
	
	        //advance the counter.
	        currentPosition = closePosition;
	    }
		
		return keys;
	}
	
	/**
	 * Return the correct part of the key and store the default value, if it has one.
	 * @param key
	 * @param defaultValuesCache
	 * @return
	 */
	private String processKey(String key, HashMap<String, String> defaultValuesCache) {
		
		int colonIndex = key.indexOf(':');
		
		if (colonIndex == -1) {
			//not in default value format.
			return key.trim();
		}
		
		String defaultValue = key.substring(colonIndex + 1).trim(); //counting the colon.
		key = key.substring(0, colonIndex).trim();
		
		//store in the cache.
		defaultValuesCache.put(key, defaultValue);
		
		return key;
	}
	
	
}
