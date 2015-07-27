package org.mule.tooling.incubator.utils.placeholder;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.incubator.utils.ProjectUtils;
import org.mule.tooling.incubator.utils.placeholder.impl.PropertiesCollector;
import org.mule.tooling.properties.extension.IPropertyKeyCompletionContributor;
import org.mule.tooling.properties.extension.PropertyKeySuggestion;

public class PropertyKeysContributor implements IPropertyKeyCompletionContributor {

	@Override
	public List<PropertyKeySuggestion> buildSuggestions(IResource currentFile) {
		
		try {
			return doBuildSuggestions(currentFile);
		} catch (Exception ex) {
			//TODO - Log
			ex.printStackTrace();
			return Collections.emptyList();
		}
		
		
	}
	
	private List<PropertyKeySuggestion> doBuildSuggestions(IResource currentFile) throws Exception {
		
		IMuleProject muleProject = ProjectUtils.safeGetMuleProjectFromProject(currentFile.getProject());
		
    	List<IFile> muleConfigs = muleProject.getConfigurationsCache().getConfigurationResources();
    	
    	List<File> configFiles = new ArrayList<File>();
    	
    	Properties props = new Properties();
    	
    	try {
    		props.load(new FileInputStream(currentFile.getLocation().toFile()));
    	} catch (Exception ex) {
    		//for some reason the file cannot be load, maybe it is new, leave it alone.
    	}
    	
    	//build the files list
    	for (IFile f : muleConfigs) {
    		configFiles.add(f.getLocation().toFile());
    	}
		
		PropertiesCollector collector = new PropertiesCollector(configFiles);
		
		Set<String> keys = collector.collectKeys();
		
		//only present new keys
		keys.removeAll(props.stringPropertyNames());
		
		List<PropertyKeySuggestion> suggestions = new ArrayList<PropertyKeySuggestion>(keys.size());
		
		for(String key : keys) {
			String def = collector.getDefaultValues().get(key);			
			suggestions.add(new PropertyKeySuggestion(key, "TODO - get file", def == null ? "?" : def));
		}
		
		return suggestions;
	}

}
