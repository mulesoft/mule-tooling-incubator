package org.mule.tooling.ui.contribution.munit.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;
import org.mule.tooling.ui.contribution.munit.MunitResourceUtils;


public class JumpToTestAction extends Action {

    private IFile file;

    public JumpToTestAction(IFile file, String name) {
        super();
        this.file = file;
        setImageDescriptor(MunitPlugin.TEST_ICON_DESCRIPTOR);
        setToolTipText("Go to test");
        setText("Jump to " + name + " Suite");
        setEnabled(true);
    }

    @Override
    public void run() {
        MunitResourceUtils.open(file);

    }

}
