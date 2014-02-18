package org.mule.tooling.ui.contribution.munit.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;

/**
 * <p>
 * Menu action to create a new tests using the test wizard
 * </p>
 */
public class CreateNewTestWizardAction extends Action {

    public CreateNewTestWizardAction() {
        super();
        setImageDescriptor(MunitPlugin.TEST_ICON_DESCRIPTOR);
        setToolTipText("Create a new test from scratch");
        setText("Create new Suite");
        setEnabled(true);
    }

    @Override
    public void run() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWizardDescriptor descriptor = workbench.getNewWizardRegistry().findWizard(MunitPlugin.TEST_WIZARD_ID);
        try {
            if (descriptor != null) {
                IWizard wizard = descriptor.createWizard();
                WizardDialog wd = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
                wd.setTitle(wizard.getWindowTitle());
                wd.open();
            }
        } catch (CoreException e) {
            MunitPlugin.log(e);
        }
    }

}
