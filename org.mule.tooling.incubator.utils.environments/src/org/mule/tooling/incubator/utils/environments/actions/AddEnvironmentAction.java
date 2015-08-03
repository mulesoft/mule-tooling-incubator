package org.mule.tooling.incubator.utils.environments.actions;

import java.util.ArrayList;
import java.util.List;

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
		
		List<String> environmentNames = new ArrayList<String>(editorProvider.getEditorModel().getEnvironmentsConfiguration().keySet());
		
		AddEnvironmentDialog envDialog = new AddEnvironmentDialog(Display.getDefault().getActiveShell(), environmentNames);
		int result = envDialog.open();
		
		if (result != AddEnvironmentDialog.CANCEL && !StringUtils.isEmpty(envDialog.getResultingKey())) {
			editorProvider.getMuleEnvironmentsEditor().addEnvironment(envDialog.getResultingKey(), envDialog.getEnvCopy());
		}
		
	}
	
}
