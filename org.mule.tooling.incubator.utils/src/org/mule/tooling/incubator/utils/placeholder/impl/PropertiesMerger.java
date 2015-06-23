package org.mule.tooling.incubator.utils.placeholder.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class PropertiesMerger {
	
	private final File outputFile;
	private final Set<String> inputKeys;
	private Set<String> deletedKeys;
	private Set<String> createdKeys;
	
	public PropertiesMerger(File outputFile, Set<String> inputKeys) {
		super();
		this.outputFile = outputFile;
		this.inputKeys = inputKeys;
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
			
			String comment = "# Content addition timestamp: " + df.format(new Date()) + "\n";
			bw.write(comment);
			
			
			for(String key : updateProps) {
				String line = key + "=?\n";
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
