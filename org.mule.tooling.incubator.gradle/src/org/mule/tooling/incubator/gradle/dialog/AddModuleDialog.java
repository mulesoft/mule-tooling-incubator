package org.mule.tooling.incubator.gradle.dialog;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddModuleDialog extends TitleAreaDialog {
	
	private Text dialogInputText;
	
	private final List<String> existingModules;
	
	private String dialogInput;
	
	public AddModuleDialog(Shell parentShell, List<String> existingModules) {
		super(parentShell);
		this.existingModules = existingModules == null ? new ArrayList<String>(): existingModules;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent = (Composite) super.createDialogArea(parent);
		
		setTitle("Add new domain Module");
		setMessage("Please enter the name of the module to add to the current Domain");
		
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
		label.setText("Module Name: ");
		
		labelDataFactory.applyTo(label);
		
		
		dialogInputText = new Text(controlArea, SWT.BORDER);		
		textDataFactory.applyTo(dialogInputText);
		
		dialogInputText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				dialogInput = dialogInputText.getText();
				validateForm();
			}
		});
		
		
		return parent;
	}
	
	public String getDialogInput() {
		return dialogInput;
	}
	
	private void validateForm() {
		
		if (dialogInput == null) {
			dialogInput = "";
		}
		
		getButton(IDialogConstants.OK_ID).setEnabled(isFormValid());
		if (existingModules.contains(dialogInput)) {
			setErrorMessage("The module: " + dialogInput + " already exists in the Domain");
			return;
		}
		
		if (StringUtils.isEmpty(dialogInput)) {
			setErrorMessage("Please enter a module name...");
			return;
		}
		
		setErrorMessage(null);
	}
	
	public boolean isFormValid() {
		return !existingModules.contains(dialogInput) && !StringUtils.isEmpty(dialogInput);
	}
	
}
