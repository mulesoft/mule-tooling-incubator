package org.mule.tooling.incubator.utils.environments.actions;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.mule.tooling.incubator.utils.environments.dialogs.AddEnvironmentDialog;
import org.mule.tooling.incubator.utils.environments.editor.IMuleEnvironmentsEditorProvider;

public class AddEnvironmentAction extends Action {
	
	private final IMuleEnvironmentsEditorProvider editorProvider;
	
	public AddEnvironmentAction(IMuleEnvironmentsEditorProvider editorProvider) {
		this.editorProvider = editorProvider;
		setToolTipText("Create a new environment for the given prefix.");
		setText("Add Environment");
	}

	@Override
	public void run() {
		AddEnvironmentDialog envDialog = new AddEnvironmentDialog(Display.getDefault().getActiveShell());
		int result = envDialog.open();
		
		if (result != AddEnvironmentDialog.CANCEL && !StringUtils.isEmpty(envDialog.getResultingKey())) {
			editorProvider.getMuleEnvironmentsEditor().addEnvironment(envDialog.getResultingKey());
		}
		
	}
	
}
