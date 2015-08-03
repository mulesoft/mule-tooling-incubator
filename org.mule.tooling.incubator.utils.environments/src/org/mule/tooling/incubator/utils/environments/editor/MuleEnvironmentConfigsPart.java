package org.mule.tooling.incubator.utils.environments.editor;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.mule.tooling.incubator.utils.environments.model.EnvironmentsSetting;

public class MuleEnvironmentConfigsPart extends SectionPart {
	
	private FormToolkit toolkit;
	
	private EnvironmentsSetting currentConfiguration;
	private HashMap<String, Text> textsTable;
	private MuleEnvironmentsEditor editor;
	private Button configureSection;
	private Section topSection;
	
	public MuleEnvironmentConfigsPart(Composite parent, FormToolkit toolkit, EnvironmentsSetting initialConfiguration) {
		super(parent, toolkit, Section.TITLE_BAR);
		getSection().setText("Environment Configuration");		
		this.toolkit = toolkit;
		currentConfiguration = initialConfiguration;
		configurePanel(getSection(), toolkit);
	}

	private void configurePanel(Section section, FormToolkit toolkit) {
		
		Composite globalPanel = toolkit.createComposite(section);
		
		ColumnLayout layout = new ColumnLayout();
		layout.maxNumColumns = 1;
		layout.minNumColumns = 1;
		
		globalPanel.setLayout(layout);
		
		textsTable = new HashMap<String, Text>();
		
		
		addTopPanel(toolkit, globalPanel);
		buildEnvironmentsPanels(toolkit, globalPanel);
		
		toolkit.paintBordersFor(globalPanel);
		section.setClient(globalPanel);
		
		updateFieldsEnablement();
	}

	private void addTopPanel(FormToolkit toolkit, Composite globalPanel) {
		topSection = toolkit.createSection(globalPanel, Section.TITLE_BAR);
		Composite sectionContent = toolkit.createComposite(topSection);
		
		topSection.setText(buildTopSectionText());
		
		sectionContent.setLayout(new RowLayout());
		configureSection = toolkit.createButton(sectionContent, "Configure this key", SWT.CHECK);
		
		//update initial activation state
		configureSection.setSelection(currentConfiguration.isPresent());
				
		configureSection.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.getSource();
				currentConfiguration.setPresent(b.getSelection());
				updateFieldsEnablement();
				//this needs to be saved
				editor.setDirty(true);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		
		toolkit.paintBordersFor(sectionContent);
		topSection.setClient(sectionContent);
		
	}

	private void buildEnvironmentsPanels(FormToolkit toolkit, Composite globalPanel) {
		
		for(String envName : currentConfiguration.getEnvironmentNames()) {
			
			Section sectionPanel = toolkit.createSection(globalPanel, Section.TITLE_BAR);
			Composite sectionContent = toolkit.createComposite(sectionPanel);
			
			final String environment = envName;
			
			sectionPanel.setText(envName);
			
			GridLayout gl = new GridLayout();
			gl.numColumns = 2;
			
			sectionContent.setLayout(gl);
			
			toolkit.createLabel(sectionContent, "Value:", SWT.BOLD);
			final Text text = toolkit.createText(sectionContent, currentConfiguration.getForEnvironment(envName));
			text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			toolkit.paintBordersFor(sectionContent);
			sectionPanel.setClient(sectionContent);
			
			text.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					
					String originalValue = currentConfiguration.getForEnvironment(environment);
					String newValue = ((Text)e.getSource()).getText();
					
					if  (originalValue == null) {
						//null value is translated to empty string in a text field.
						originalValue = "";
					}
					
					if (!StringUtils.equals(originalValue,newValue)) {
						currentConfiguration.setForEnvironment(environment, newValue);
						editor.setDirty(true);
					}
				}
			});
			
			textsTable.put(environment, text);
		}
	}
	
	public void repaintPanels() {
		getSection().getClient().dispose();
		configurePanel(getSection(), toolkit);
		getSection().layout(true);
		refresh();
	}

	public EnvironmentsSetting getCurrentConfiguration() {
		
		if (!currentConfiguration.isPresent()) {
			//no need to update
			return currentConfiguration;
		}
		
		for(String env : currentConfiguration.getEnvironmentNames()) {
			currentConfiguration.setForEnvironment(env, textsTable.get(env).getText());
		}
		
		return currentConfiguration;
	}

	public void setCurrentConfiguration(EnvironmentsSetting currentConfiguration) {

		this.currentConfiguration = currentConfiguration;
		
		this.topSection.setText(buildTopSectionText());
		this.configureSection.setSelection(currentConfiguration.isPresent());
		
		//easiest indicator that this needs to be repainted.
		if (currentConfiguration.getEnvironmentNames().size() != textsTable.size()) {
			System.out.println("Had to repaint!!");
			repaintPanels();
			return;
		}
		
		for(String env : currentConfiguration.getEnvironmentNames()) {
			if (!textsTable.containsKey(env)) {
				//this is a different set of environments!!
				System.out.println("Had to repaint!!");
				repaintPanels();
				return;
			}
			
			if (!StringUtils.isEmpty(currentConfiguration.getForEnvironment(env))) {
				textsTable.get(env).setText(currentConfiguration.getForEnvironment(env));
			} else {
				textsTable.get(env).setText("");
			}
			
		}
		updateFieldsEnablement();
	}
	
	private void updateFieldsEnablement() {
		
		for(Text t : textsTable.values()) {
			t.setEnabled(configureSection.getSelection());
		}
		
		configureSection.setEnabled(!StringUtils.isEmpty(currentConfiguration.getKey()));
		

	}
	
	private String buildTopSectionText() {
		return "Key: " + currentConfiguration.getKey();
	}
	
	public MuleEnvironmentsEditor getEditor() {
		return editor;
	}

	public void setEditor(MuleEnvironmentsEditor editor) {
		this.editor = editor;
	}
	
	
}
