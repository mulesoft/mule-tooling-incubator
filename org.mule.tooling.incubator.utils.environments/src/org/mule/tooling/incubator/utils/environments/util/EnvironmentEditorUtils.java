package org.mule.tooling.incubator.utils.environments.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.mule.tooling.incubator.utils.environments.api.EnvironmentsEditorToolbarExtension;

public class EnvironmentEditorUtils {
	
	public static final String INTERNAL_TOOLBAR_EXTENSION_ID = "org.mule.tooling.incubator.utils.environments.internalToolbar";
	
	public static List<EnvironmentsEditorToolbarExtension> loadInternalToolbarExtensions() {
		IConfigurationElement[] configs = Platform.getExtensionRegistry().getConfigurationElementsFor(INTERNAL_TOOLBAR_EXTENSION_ID);
		
		if (configs == null) {
			return Collections.emptyList();
		}
		
		ArrayList<EnvironmentsEditorToolbarExtension> ret = new ArrayList<EnvironmentsEditorToolbarExtension>(configs.length);
		
		
		for(IConfigurationElement element : configs) {
			try {
				ret.add((EnvironmentsEditorToolbarExtension) element.createExecutableExtension("class"));
			} catch (CoreException ex) {
				ex.printStackTrace();
			}
		}
		
		return ret;
	}
	
}
