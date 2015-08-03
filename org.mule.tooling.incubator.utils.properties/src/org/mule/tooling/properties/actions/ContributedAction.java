package org.mule.tooling.properties.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.Action;
import org.mule.tooling.properties.editors.MultiPagePropertiesEditorContributor;
import org.mule.tooling.properties.extension.PropertiesEditorAction;

public class ContributedAction extends Action {
	
	private final PropertiesEditorAction action;
	
	public ContributedAction(IConfigurationElement config, MultiPagePropertiesEditorContributor editor) {
		
		//extract settings
		String text = config.getAttribute("text");
		String tooltip = config.getAttribute("tooltip");
		
		if (text != null) {
			setText(text);
		}
		
		if (tooltip != null) {
			setToolTipText(tooltip);
		}
		
		PropertiesEditorAction currentAction = null;
		
		try {
			if (config.getAttribute("class") != null) {
				currentAction = (PropertiesEditorAction) config.createExecutableExtension("class");
				currentAction.setEditor(editor);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		action = currentAction;
	}
	
	@Override
	public void run() {
		
		if (action != null) {
			action.run();
		}
	}
}
