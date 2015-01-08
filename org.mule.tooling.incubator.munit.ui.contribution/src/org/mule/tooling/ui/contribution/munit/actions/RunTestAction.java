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
import org.mule.tooling.ui.contribution.munit.MunitPlugin;
import org.mule.tooling.ui.contribution.munit.MunitResourceUtils;
import org.mule.tooling.ui.contribution.munit.editors.MunitMultiPageEditor;
import org.mule.tooling.ui.contribution.munit.runner.MunitLaunchConfigurationConstants;

/**
 * <p>
 * Run action, it can be used for Debug and Run
 * </p>
 */
public class RunTestAction extends Action {
    public static final String RUN_MODE = "run";
    public static final String DEBUG_MODE = "debug";

    private String mode;
    private String perspective;

    public static void runOrdebugMunitTest(String perspective, String mode) {
        IWorkbenchWindow activeWorkbenchWindow = MunitPlugin.getActiveWorkbenchWindow();
        if (activeWorkbenchWindow == null) {
            return;
        }

        try {
            MunitResourceUtils.openMunitRunner();
            if (perspective != null) {
                PlatformUI.getWorkbench().showPerspective(perspective, activeWorkbenchWindow);
            }
        } catch (WorkbenchException e1) {
            MunitPlugin.log(e1);
        }

        IEditorPart activeEditor = activeWorkbenchWindow.getActivePage().getActiveEditor();
        if (activeEditor instanceof MunitMultiPageEditor) {
            MultiPageMessageFlowEditor editor = (MultiPageMessageFlowEditor) activeEditor;
            MessageFlowEditor messageFlowEditor = editor.getFlowEditor();
            IFile configFileFromFlowFile = messageFlowEditor.getInputXmlConfigFile();
            MunitLaunchConfigurationConstants.runTest(configFileFromFlowFile, mode);
        }
    }

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
        runOrdebugMunitTest(perspective, mode);
    }
}
