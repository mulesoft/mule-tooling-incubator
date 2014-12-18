package org.mule.tooling.ui.contribution.munit.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.messageflow.editor.MessageFlowEditor;
import org.mule.tooling.messageflow.editor.MultiPageMessageFlowEditor;
import org.mule.tooling.messageflow.editpart.EntityEditPart;
import org.mule.tooling.messageflow.editpart.MuleConfigurationEditPart;
import org.mule.tooling.messageflow.events.RefreshRequestedEvent;
import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;
import org.mule.tooling.ui.contribution.munit.MunitResourceUtils;

/**
 * <p>
 * Creates a new Munit suite file under the {@link MunitPlugin#MUNIT_FOLDER_PATH} path. The file has the same structure as the empty mule project.
 * </p>
 */
public class CreateTestAction extends Action {

    public CreateTestAction(MessageFlowNode selected, String name) {
        super();
        setImageDescriptor(MunitPlugin.TEST_ICON_DESCRIPTOR);
        setToolTipText("Create Munit Suite File to start writting your Munit tests.");
        setText("Create new " + name + " Suite");
        setEnabled(true);
    }

    @Override
    public void run() {
        final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow == null) {
            return;
        }
        MunitResourceUtils.openMunitRunner();
        final IEditorPart activeEditor = activeWorkbenchWindow.getActivePage().getActiveEditor();
        if (activeEditor instanceof MultiPageMessageFlowEditor) {
            final MultiPageMessageFlowEditor editor = (MultiPageMessageFlowEditor) activeEditor;
            final MessageFlowEditor messageFlowEditor = editor.getFlowEditor();
            IMuleProject muleProject = messageFlowEditor.getMuleProject();
            
            MunitResourceUtils.configureProjectForMunit(muleProject);
            IFolder munitFolder = createMunitFolder(activeWorkbenchWindow, muleProject);
            createMunitFile(activeWorkbenchWindow, messageFlowEditor, munitFolder);
        }

    }

    private void createMunitFile(final IWorkbenchWindow activeWorkbenchWindow, final MessageFlowEditor messageFlowEditor, IFolder munitFolder) {
        IFile munitConfigFile = MunitResourceUtils.createMunitFileFromXmlConfigFile(munitFolder, messageFlowEditor.getInputXmlConfigFile());
        if (!munitConfigFile.exists()) {
            try {
            	MunitResourceUtils.createXMLConfigurationFromTemplate(messageFlowEditor.getMuleProject(), munitConfigFile.getName(),
            			munitConfigFile.getName().replace("-test", ""), munitFolder);

            	openMunitEditor(messageFlowEditor, munitConfigFile);
            } catch (Throwable e) {
                MessageDialog.openError(activeWorkbenchWindow.getShell(), "Munit Suite file creation error", "Could not create a new Suite File for your Munit tests.");
            }
        }
    }
    
    private void openMunitEditor(MessageFlowEditor messageFlowEditor,  IFile munitConfigFile) throws CoreException{
    	StructuredSelection selection = (StructuredSelection) messageFlowEditor.getSelection();
        EntityEditPart<?> editPart = (EntityEditPart<?>) selection.getFirstElement();
        editPart = getParent(editPart);
        
        
        IEditorPart openEditor = MunitResourceUtils.open(munitConfigFile);
        MultiPageMessageFlowEditor multiPageEditor = (MultiPageMessageFlowEditor) openEditor;
        MessageFlowEditor flowEditor = multiPageEditor.getFlowEditor();
        
        MunitResourceUtils.createDefaultMunitTest(flowEditor, ((MessageFlowNode) editPart.getEntity()).getName());
        
        multiPageEditor.updateFlowFromSource();
        multiPageEditor.openFlowEditorPage();
    }

    private EntityEditPart<?> getParent(EntityEditPart<?> editPart) {
        if (editPart.getParent() instanceof MuleConfigurationEditPart) {
            return editPart;
        }
        return getParent((EntityEditPart<?>) editPart.getParent());
    }

    private IFolder createMunitFolder(final IWorkbenchWindow activeWorkbenchWindow, IMuleProject muleProject) {
        try {
            return MunitResourceUtils.createMunitFolder(muleProject);
        } catch (CoreException e) {
            MessageDialog.openError(activeWorkbenchWindow.getShell(), "Munit folder creation error", "Could not create a new folder in your project for Munit tests");
        }
        return null;
    }

    public static void openConfigWithConfigName(IMuleProject muleProject, String toFind) {
        try {
            for (IFile xmlFile : muleProject.getConfigurationsCache().getConfigurationResources()) {
                String nameWithoutSuffix = MunitResourceUtils.getBaseName(xmlFile);
                if (nameWithoutSuffix.equalsIgnoreCase(toFind)) {
                    MunitResourceUtils.open(xmlFile);
                    return;
                }
            }
            throw new RuntimeException("Couldn't find a config file that matches the name " + toFind);
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

}
