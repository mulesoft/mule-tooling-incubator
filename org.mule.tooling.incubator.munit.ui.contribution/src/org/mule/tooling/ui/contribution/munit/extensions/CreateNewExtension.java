package org.mule.tooling.ui.contribution.munit.extensions;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.core.MuleConfigurationsCache;
import org.mule.tooling.core.MuleConfigurationsCache.MuleConfigurationEntry;
import org.mule.tooling.messageflow.editor.IMessageFlowNodeContextMenuProvider;
import org.mule.tooling.messageflow.editor.MultiPageMessageFlowEditor;
import org.mule.tooling.model.messageflow.MessageFlowEntity;
import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.MuleConfiguration;
import org.mule.tooling.ui.contribution.munit.actions.CreateNewTestWizardAction;
import org.mule.tooling.ui.contribution.munit.actions.CreateTestAction;
import org.mule.tooling.ui.contribution.munit.actions.JumpToTestAction;
import org.mule.tooling.ui.contribution.munit.editors.ImportedFilesVisitor;
import org.mule.tooling.ui.contribution.munit.editors.MunitMultiPageEditor;

/**
 * <p>
 * Extension in order to create a new test, only applicable to flows and subflows.
 * </p>
 */
public class CreateNewExtension implements IMessageFlowNodeContextMenuProvider {

    @Override
    public void addActionsForNode(IMenuManager menu, MessageFlowNode selected) {
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

        if (comesFromRightEditor(activePage)) {
            MultiPageMessageFlowEditor editor = (MultiPageMessageFlowEditor) activePage.getActiveEditor();
            menu.add(new Separator("Test"));
            MenuManager wrapInMenu = new MenuManager("Munit", "Munit");
            menu.appendToGroup("Test", wrapInMenu);
            MuleConfigurationsCache cache = MuleConfigurationsCache.getDefaultInstance();
            String testName = editor.getFileName().replace(".mflow", "-test");
            boolean found = false;
            List<MuleConfigurationEntry> configurationEntries = cache.getConfigurationEntries(editor.getFlowEditor().getMuleProject());
            for (MuleConfigurationEntry entry : configurationEntries) {
                if (isImportedMuleConfiguraiton(entry.getMuleConfiguration(), editor.getFlowEditor().getMuleConfiguration().getName())) {
                    wrapInMenu.add(new JumpToTestAction(entry.getConfigurationFile(), entry.getMuleConfiguration().getName()));
                    found = true;
                }
            }

            if (!found) {
                wrapInMenu.add(new CreateTestAction(selected, testName));
            } else {
                wrapInMenu.add(new Separator("Create Test"));
                wrapInMenu.add(new CreateNewTestWizardAction());
            }
        }
    }

    protected boolean comesFromRightEditor(IWorkbenchPage activePage) {

        if (activePage != null) {
            return !(activePage.getActiveEditor() instanceof MunitMultiPageEditor);
        }

        return false;
    }

    public boolean isImportedMuleConfiguraiton(MuleConfiguration muleConfiguration, String referencedFile) {
        List<JAXBElement<? extends MessageFlowEntity>> globalEntries = muleConfiguration.getGlobalEntries();

        ImportedFilesVisitor visitor = new ImportedFilesVisitor();
        for (JAXBElement<? extends MessageFlowEntity> globalEntry : globalEntries) {
            globalEntry.getValue().accept(visitor);
        }

        List<String> importedFiles = visitor.getFiles();

        for (String importedFile : importedFiles) {
            if (importedFile.contains(referencedFile)) {
                return true;
            }
        }
        return false;
    }

}
