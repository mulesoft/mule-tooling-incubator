package org.mule.tooling.devkit.wizards;

import org.eclipse.jface.viewers.ISelection;

public class ConnectorNewWizardPage extends ModuleNewWizardPage {

    public ConnectorNewWizardPage(ISelection selection) {
        super(selection);
        setTitle("Cloud Connector Wizard");
        setDescription("This wizard creates a Mule Cloud Connector template");
        this.selection = selection;
        this.nameLabel = "&Connector Name:";
    }

}