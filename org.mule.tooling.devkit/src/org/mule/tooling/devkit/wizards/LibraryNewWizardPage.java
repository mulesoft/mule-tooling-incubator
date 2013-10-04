package org.mule.tooling.devkit.wizards;

import org.eclipse.jface.viewers.ISelection;

public class LibraryNewWizardPage extends ModuleNewWizardPage {

    public LibraryNewWizardPage(ISelection selection) {
        super(selection);
        setTitle("Mule Library Wizard");
        setDescription("This wizard creates a Mule Library template");
        this.selection = selection;
        this.nameLabel = "&Library Name:";
    }

}
