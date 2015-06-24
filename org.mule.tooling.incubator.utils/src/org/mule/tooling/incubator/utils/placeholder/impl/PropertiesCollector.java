package org.mule.tooling.incubator.utils.placeholder.impl;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class PropertiesCollector {
	
	private final Collection<File> inputFiles;
	
	private HashMap<String, String> defaultValues;
	private HashMap<String, Set<String>> keyPositions;
	
	public PropertiesCollector(Collection<File> files) {
		this.inputFiles = files;
	}
	
	public Set<String> collectKeys() throws Exception {
		
		Set<String> ret = new TreeSet<>();
		defaultValues = new HashMap<>();
		
		//do nothing
		if (inputFiles == null || inputFiles.isEmpty()) {
			return ret;
		}
		
		for(File file : inputFiles) {
			String fileContents = new String(Files.readAllBytes(file.toPath()));
			PropertyPlaceholderExtractor extractor = new PropertyPlaceholderExtractor(fileContents);			
			ret.addAll(extractor.extractFileKeys(defaultValues));			
		}
		
		
		return ret;
	}

	public HashMap<String, String> getDefaultValues() {
		return defaultValues;
	}

	public HashMap<String, Set<String>> getKeyPositions() {
		return keyPositions;
	}
	
}
