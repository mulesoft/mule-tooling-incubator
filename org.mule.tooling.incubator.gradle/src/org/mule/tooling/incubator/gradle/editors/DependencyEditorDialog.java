package org.mule.tooling.incubator.gradle.editors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.incubator.gradle.model.StudioDependency;

public class DependencyEditorDialog extends Dialog {
	
	private StudioDependency dep;
	private Text groupField;
	private Text nameField;
	private Text versionField;
	
	protected DependencyEditorDialog(Shell parentShell, StudioDependency data) {
		super(parentShell);
		dep = data;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("Cofigure Dependency");
		newShell.setSize(400, 200);
		super.configureShell(newShell);
		
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Control ret = super.createDialogArea(parent);
		Composite dialogArea = (Composite) ret;
		
		GridLayout layout = new GridLayout(2, false);
		layout.marginRight = 5;
		layout.marginLeft = 5;
		
		dialogArea.setLayout(layout);
		//parent.setLayout(new GridLayout(1,true));
		
//		GridData generalData = new GridData(GridData.FILL);
//		ret.setLayoutData(generalData);
//		
		GridData labelData = new GridData();
		labelData.horizontalSpan = 1;
		labelData.widthHint = 100;
		GridData textData = new GridData(GridData.GRAB_HORIZONTAL);
		textData.widthHint = 340;
		
		
		Label l = new Label(dialogArea, SWT.NONE);
		l.setText("Group: ");
		l.setLayoutData(labelData);
		
		groupField = new Text(dialogArea, SWT.BORDER);
		groupField.setLayoutData(textData);
		
		l =new Label(dialogArea, SWT.NONE); 
		l.setText("Name: ");
		l.setLayoutData(labelData);
		
		
		nameField = new Text(dialogArea, SWT.BORDER);
		nameField.setLayoutData(textData);
		
		l = new Label(dialogArea, SWT.NONE); 
		l.setText("Version: ");
		l.setLayoutData(labelData);
		
		
		versionField = new Text(dialogArea, SWT.BORDER);
		versionField.setLayoutData(textData);
		
		setValues();
		
		return ret;
	}

	private void setValues() {
		groupField.setText(dep.getGroup() != null? dep.getGroup() : "");
		nameField.setText(dep.getName() != null? dep.getName() : "");
		versionField.setText(dep.getVersion() != null? dep.getVersion() : "");
	}
	
	@Override
	protected void okPressed() {
		
		dep.setGroup(groupField.getText());
		dep.setName(nameField.getText());
		dep.setVersion(versionField.getText());
		
		super.okPressed();
	}
	
	public StudioDependency getDep() {
		return dep;
	}
	
}
