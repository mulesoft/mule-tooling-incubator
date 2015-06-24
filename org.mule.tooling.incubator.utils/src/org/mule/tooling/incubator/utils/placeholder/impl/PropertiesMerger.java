package org.mule.tooling.incubator.utils.placeholder.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class PropertiesMerger {
	
	private final File outputFile;
	private final Set<String> inputKeys;
	private final HashMap<String, String> defaults;
	private Set<String> deletedKeys;
	private Set<String> createdKeys;
	
	public PropertiesMerger(File outputFile, Set<String> inputKeys, HashMap<String, String> defaults) {
		this.outputFile = outputFile;
		this.inputKeys = inputKeys;
		this.defaults = defaults == null ? new HashMap<String, String>() : defaults;
	}
	
	
	public void doMerge() throws Exception {
		
		Properties props = new Properties();
		props.load(new FileInputStream(outputFile));
		Set<String> origKeys = props.stringPropertyNames(); 
		
		createdKeys = new TreeSet<String>(inputKeys);
		deletedKeys = new TreeSet<String>(origKeys);
		
		//remove all the properties found in orig keys
		createdKeys.removeAll(origKeys);
		
		//remove all the properties found in inputkeys
		deletedKeys.removeAll(inputKeys);
		
		System.out.println("UPDATED KEYS: ");
		System.out.println(createdKeys);
		
		if (createdKeys.isEmpty()) {
			return;
		}
		
		outputUpdatesInFile(createdKeys);
		
	}


	private void outputUpdatesInFile(Set<String> updateProps) throws Exception {
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, true))) {
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			String comment = "\n# Content addition timestamp: " + df.format(new Date()) + "\n";
			bw.write(comment);
			
			
			for(String key : updateProps) {
				String value = defaults.containsKey(key) ? defaults.get(key) : "?";
				String line = key + "=" + value + "\n";
				bw.write(line);
			}
			
		}
		
	}


	public Set<String> getDeletedKeys() {
		return deletedKeys;
	}


	public Set<String> getCreatedKeys() {
		return createdKeys;
	}
	
}
