package org.mule.tooling.ui.contribution.munit.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.mule.tooling.messageflow.editor.MessageFlowEditor;
import org.mule.tooling.messageflow.editor.MultiPageMessageFlowEditor;
import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.ui.contribution.munit.MunitResourceUtils;
import org.mule.tooling.ui.contribution.munit.editors.MunitMultiPageEditor;
import org.mule.tooling.ui.contribution.munit.runner.MunitLaunchConfigurationConstants;
import org.mule.tooling.ui.contribution.munit.runner.MunitTestRunnerViewPart;


public class RunTestAction  extends Action {

	
    private String mode;
	private String perspective;

	public RunTestAction(MessageFlowNode selected, String message, String mode, String perspective, ImageDescriptor imageDescriptor) {
        super();
		this.mode = mode;
		this.perspective = perspective;
        setImageDescriptor(imageDescriptor);
        setToolTipText("Run this Munit suite");
        setText(message);
        setEnabled(true);
    }

    @Override
    public void run() {
        final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow == null) {
            return;
        }
        try {
        	MunitResourceUtils.openMunitRunner();
        	if ( perspective != null ){
        		 PlatformUI.getWorkbench().showPerspective(perspective, activeWorkbenchWindow);
        	}
           
        } catch (WorkbenchException e1) {
            e1.printStackTrace();
        }

        final IEditorPart activeEditor = activeWorkbenchWindow.getActivePage().getActiveEditor();
        if (activeEditor instanceof MunitMultiPageEditor) {
            final MultiPageMessageFlowEditor editor = (MultiPageMessageFlowEditor) activeEditor;
            final MessageFlowEditor messageFlowEditor = editor.getFlowEditor();
           IFile configFileFromFlowFile = MunitTestRunnerViewPart.getConfigFileFromFlowFile(messageFlowEditor.getMuleProject().getJavaProject().getProject(), messageFlowEditor.getInputFile());
           MunitLaunchConfigurationConstants.runTest(configFileFromFlowFile, mode);
        }

    }
}
