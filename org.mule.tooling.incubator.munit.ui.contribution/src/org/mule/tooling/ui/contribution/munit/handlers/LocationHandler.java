package org.mule.tooling.ui.contribution.munit.handlers;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.EditPart;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.messageflow.editor.MessageFlowEditor;
import org.mule.tooling.messageflow.editor.MultiPageMessageFlowEditor;
import org.mule.tooling.messageflow.editpart.EntityEditPart;
import org.mule.tooling.model.messageflow.Flow;

public class LocationHandler {
	private static LocationHandler instance;
	
	public static void openConfigWithConfigName(IMuleProject muleProject, String toFind) {
		try {
			for (IFile mflowFile : muleProject.getConfigurationManager().getConfigurationResources()) {
				String nameWithoutSuffix = mflowFile.getName().replaceAll(".mflow", "");
				if (nameWithoutSuffix.equalsIgnoreCase(toFind)) {
					openEditor(mflowFile);
					return;
				}
			}
			throw new RuntimeException("Couldn't find a config file that matches the name " + toFind);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	private static void openEditor(IFile fileToOpen) throws PartInitException {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			IDE.openEditor(page, fileToOpen);
		} catch (PartInitException e) {
		}
	}

	public static EntityEditPart<?> selectFlow(String flowName) {

		final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null) {
			return null;
		}
		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		IEditorPart activeEditor = activePage.getActiveEditor();
		if (activeEditor instanceof MultiPageMessageFlowEditor) {
			MultiPageMessageFlowEditor editorPart = (MultiPageMessageFlowEditor) activeEditor;

			if (editorPart.getCurentPageIndex() != editorPart.getFlowEditorIndex()) {
				editorPart.openFlowEditorPage();
			}
			MessageFlowEditor flowEditor = editorPart.getFlowEditor();
			return selectFlowByName(flowEditor, flowName);
		}
		return null;
	}
	
	public static EntityEditPart<?> select(EditPart editPart) {

		final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null) {
			return null;
		}
		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		IEditorPart activeEditor = activePage.getActiveEditor();
		if (activeEditor instanceof MultiPageMessageFlowEditor) {
			MultiPageMessageFlowEditor editorPart = (MultiPageMessageFlowEditor) activeEditor;

			if (editorPart.getCurentPageIndex() != editorPart.getFlowEditorIndex()) {
				editorPart.openFlowEditorPage();
			}
			MessageFlowEditor flowEditor = editorPart.getFlowEditor();
			flowEditor.getViewer().select(editPart);
			flowEditor.getViewer().reveal(editPart);
		}
		return null;
	}
	
	public static EntityEditPart<?> selectFlowByName(MessageFlowEditor flowEditor, String flowName) {
        List<Flow> flows = flowEditor.getMuleConfiguration().getFlows();
        for (Flow flow : flows) {
            if ((flow.getName() != null) && (flow.getName().equals(flowName))) {
                EntityEditPart<?> match = MessageFlowEditor.findEditPartInRegistry(flowEditor.getViewer(), flow);
                if (match != null) {
                	flowEditor.getViewer().select(match);
                	flowEditor.getViewer().reveal(match);
                	
                	return match;
                }
            }
        }
        
        return null;
    }
	
	


}
