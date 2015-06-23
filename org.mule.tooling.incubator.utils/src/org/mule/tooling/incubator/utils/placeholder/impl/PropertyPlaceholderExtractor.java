package org.mule.tooling.incubator.utils.placeholder.impl;

import java.util.HashSet;
import java.util.Set;

public class PropertyPlaceholderExtractor {
	
	private final String fileContents;
	
    private static final String PLACEHOLDER_START = "${";
    private static final String PLACEHOLDER_END = "}";
    
	public PropertyPlaceholderExtractor(String fileContents) {
		this.fileContents = fileContents;
	}
	
	public Set<String> extractFileKeys() {
		
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
	        
	        keys.add(key.trim());
	
	        //advance the counter.
	        currentPosition = closePosition;
	    }
		
		return keys;
	}
	
}
