package org.mule.tooling.incubator.utils.placeholder.impl;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class PropertiesCollector {
	
	private final Collection<File> inputFiles;
	
	public PropertiesCollector(Collection<File> files) {
		this.inputFiles = files;
	}
	
	public Set<String> collectKeys() throws Exception {
		
		Set<String> ret = new TreeSet<String>();
		
		//do nothing
		if (inputFiles == null || inputFiles.isEmpty()) {
			return ret;
		}
		
		for(File file : inputFiles) {
			String fileContents = new String(Files.readAllBytes(file.toPath()));
			PropertyPlaceholderExtractor extractor = new PropertyPlaceholderExtractor(fileContents);			
			ret.addAll(extractor.extractFileKeys());			
		}
		
		
		return ret;
	}
	
	
}
