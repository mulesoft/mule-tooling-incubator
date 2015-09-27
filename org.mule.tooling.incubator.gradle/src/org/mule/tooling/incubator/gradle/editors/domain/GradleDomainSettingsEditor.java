package org.mule.tooling.incubator.gradle.editors.domain;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.mule.tooling.incubator.gradle.model.GradleSettings;
import org.mule.tooling.ui.MuleImages;

public class GradleDomainSettingsEditor extends FormPage {
	
	public static final String FORM_PAGE_ID = "org.mule.tooling.gradle.domainConfigPage";
	
	private DomainProjectsSectionPart projectsPart;
	private GradleSettings model;
	
	public GradleDomainSettingsEditor(FormEditor editor, String title, GradleSettings model) {
		super(editor, FORM_PAGE_ID, title);
		this.model = model;
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText(getTitle());
		form.setImage(MuleImages.GLOBALS_TITLE_IMAGE);
		
		
		//configure the layout of the form
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		
		GridData configData = new GridData(GridData.FILL_BOTH);
		configData.horizontalSpan = 2;
		
		//to be fixed
		form.getBody().setLayout(layout);
		
		projectsPart = new DomainProjectsSectionPart(form.getBody(), managedForm.getToolkit(), model.getModules(), model.getProject());
		projectsPart.getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
	}
}
