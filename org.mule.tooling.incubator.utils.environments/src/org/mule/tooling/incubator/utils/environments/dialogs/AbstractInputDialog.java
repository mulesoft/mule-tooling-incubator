package org.mule.tooling.incubator.utils.environments.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractInputDialog extends TitleAreaDialog {
	
	private Text keyText;
	private String text;
	
	public AbstractInputDialog(Shell parentShell) {
		super(parentShell);
		
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent = (Composite) super.createDialogArea(parent);
		
		
		//draw the dialog
		
		Composite controlArea = new Composite(parent, SWT.NONE);
		
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(controlArea);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).applyTo(controlArea);
		
		//to layout labels
		GridDataFactory labelDataFactory = GridDataFactory.fillDefaults()
				.align(SWT.END, SWT.CENTER);
		
		//to layout texts
		GridDataFactory textDataFactory = GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.CENTER).grab(true, false);
		
		
		Label label = new Label(controlArea, SWT.NONE);
		label.setText(getInputLabel());
		
		labelDataFactory.applyTo(label);
		
		
		keyText = new Text(controlArea, SWT.BORDER);		
		textDataFactory.applyTo(keyText);
		
		return parent;
	}
	
	@Override
	protected void okPressed() {
		this.text = keyText.getText();
		super.okPressed();
	}
	
	public String getResultingKey() {
		return text;
	}
	
	protected abstract String getInputLabel();
	
}
