package org.mule.tooling.incubator.utils.environments.api;

import org.mule.tooling.incubator.utils.environments.model.EnvironmentsConfiguration;

public interface IEnvironmentsEditorContext {
	
	void setDirty(boolean dirty);
	
	void refreshUi();
	
	EnvironmentsConfiguration getCurrentConfiguration();
}
