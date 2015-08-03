package org.mule.tooling.incubator.utils.environments.api;

import org.eclipse.swt.widgets.ToolBar;

public abstract class EnvironmentsEditorToolbarExtension {
	
	public final void performCustomizeToolbar(ToolBar toolbar, IEnvironmentsEditorContext context) {
		try {
			customizeToolbar(toolbar, context);;
		} catch (Exception ex) {
			System.err.println("Error while executing extension.");
			ex.printStackTrace();
		}
	}
	
	protected abstract void customizeToolbar(ToolBar toolbar, IEnvironmentsEditorContext context) throws Exception;
	
}
