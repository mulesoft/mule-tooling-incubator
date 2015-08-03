package org.mule.tooling.incubator.utils.environments.dialogs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class AddKeyDialog extends AbstractInputDialog {
	
	private String keyPrefix;
	
	
	public AddKeyDialog(Shell parentShell, String keyPrefix) {
		super(parentShell);
		this.keyPrefix = keyPrefix;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Add a new Envoronment Key");
		setMessage("Type the key to create in the current environments.");
		
		Control ret = super.createDialogArea(parent);
		
		if (keyPrefix != null) {
			dialogInputText.setText(keyPrefix + ".");
		}
		
		return ret;
	}

	@Override
	protected String getInputLabel() {
		return "Key: ";
	}
	
}
