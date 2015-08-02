package org.mule.tooling.incubator.utils.environments.api;

import org.eclipse.swt.widgets.ToolBar;

public abstract class EnvironmentsEditorToolbarExtension {
	
	protected abstract void customizeToolbar(ToolBar toolbar, IEnvironmentsEditorContext context) throws Exception;
	
}
