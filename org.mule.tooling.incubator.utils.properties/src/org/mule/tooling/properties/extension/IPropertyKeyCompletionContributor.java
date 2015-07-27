package org.mule.tooling.properties.extension;

import java.util.List;

import org.eclipse.core.resources.IResource;

public interface IPropertyKeyCompletionContributor {
	
	public List<PropertyKeySuggestion> buildSuggestions(IResource currentFile);
	
}
