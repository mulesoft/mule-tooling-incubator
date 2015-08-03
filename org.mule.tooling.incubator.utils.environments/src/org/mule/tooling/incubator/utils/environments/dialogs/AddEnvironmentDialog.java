package org.mule.tooling.incubator.utils.environments.dialogs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class AddEnvironmentDialog extends AbstractInputDialog {

	public AddEnvironmentDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Add a new Envoronment");
		setMessage("Type the suffix string for your environment. I.E: 'dev' or 'test'.");
		
		return super.createDialogArea(parent);
	}

	@Override
	protected String getInputLabel() {
		return "Environment suffix: ";
	}

}
