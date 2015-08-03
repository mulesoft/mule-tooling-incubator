package org.mule.tooling.incubator.utils.environments.dialogs;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class AddEnvironmentDialog extends AbstractInputDialog {
	
	private String envCopy;
	private List<String> envNames;
	private Combo envCombo;
	
	public AddEnvironmentDialog(Shell parentShell, List<String> envNames) {
		super(parentShell);
		if (envNames != null) {
			this.envNames = envNames;
		} else {
			this.envNames = Collections.emptyList();
		}
		
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
	
	@Override
	protected void addAdditionalFields(Composite controlArea, GridDataFactory labelDataFactory, GridDataFactory fieldDataFactory) {
		
		final Button check = new Button(controlArea, SWT.CHECK);
		check.setText("Copy settings from environment");
		check.setEnabled(!envNames.isEmpty());
		
		GridDataFactory.swtDefaults().span(2, 0).applyTo(check);
		
		//the label
		Label envLabels = new Label(controlArea, SWT.NONE);
		envLabels.setText("Environment: ");
		labelDataFactory.applyTo(envLabels);
		
		//the combo
		envCombo = new Combo(controlArea, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		
		fieldDataFactory.applyTo(envCombo);
				
		for(String env : envNames) {
			envCombo.add(env);
			if (StringUtils.isEmpty(envCombo.getText())) {
				envCombo.setText(env);
			}
		}
		
		envCombo.setEnabled(false);
		
		//behavior to enable/disable
		check.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				envCombo.setEnabled(check.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
	}
	
	@Override
	protected void okPressed() {
		
		if (envCombo.isEnabled()) {
			envCopy = envCombo.getText();
		} else {
			envCopy = null;
		}
		super.okPressed();		
	}
	
	public String getEnvCopy() {
		return envCopy;
	}
	
}
