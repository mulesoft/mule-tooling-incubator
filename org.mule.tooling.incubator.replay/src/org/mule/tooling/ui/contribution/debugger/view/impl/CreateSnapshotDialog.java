package org.mule.tooling.ui.contribution.debugger.view.impl;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.ui.widgets.util.WidgetUtils;

public class CreateSnapshotDialog extends TitleAreaDialog {

    private Text nameText;
    private String name;

    public CreateSnapshotDialog(Shell parentShell) {
        super(parentShell);

    }

    @Override
    public void create() {
        super.create();
        // Set the title
        setTitle("Take Snapshot");
        // Set the message
        setMessage("Type the name of the new Snapshot", IMessageProvider.INFORMATION);

    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected Point getInitialSize() {

        return super.getInitialSize();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(new GridLayout(2, false));
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        nameText = new WidgetUtils().createComponentWithLabel(Text.class, container, "Name");
        return super.createDialogArea(parent);
    }

    @Override
    protected void okPressed() {
        name = nameText.getText();
        super.okPressed();
    }

    public String getName() {
        return name;
    }

}