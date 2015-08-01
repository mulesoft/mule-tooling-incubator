package org.mule.tooling.incubator.utils.environments.dialogs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class AddKeyDialog extends AbstractInputDialog {

	public AddKeyDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Add a new Envoronment Key");
		setMessage("Type the key to create (or append) to the existing environment.");
		
		return super.createDialogArea(parent);
	}

	@Override
	protected String getInputLabel() {
		return "Key: ";
	}
	
}
