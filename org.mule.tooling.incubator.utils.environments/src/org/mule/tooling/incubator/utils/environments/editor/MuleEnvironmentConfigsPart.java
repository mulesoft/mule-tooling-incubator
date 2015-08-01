package org.mule.tooling.incubator.utils.environments.editor;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
		
		
		toolkit.paintBordersFor(globalPanel);
		section.setClient(globalPanel);
	}
	
	public void repaintPanels() {
		getSection().getClient().dispose();
		configurePanel(getSection(), toolkit);
		getSection().layout(true);
		refresh();
	}

	public EnvironmentsSetting getCurrentConfiguration() {
		
		for(String env : currentConfiguration.getEnvironmentNames()) {
			currentConfiguration.setForEnvironment(env, textsTable.get(env).getText());
		}
		
		return currentConfiguration;
	}

	public void setCurrentConfiguration(EnvironmentsSetting currentConfiguration) {

		this.currentConfiguration = currentConfiguration;
		
		for(String env : currentConfiguration.getEnvironmentNames()) {
			if (!textsTable.containsKey(env)) {
				//this is a different set of environments!!
				System.out.println("Had to repaint!!");
				repaintPanels();
				break;
			}
			
			if (!StringUtils.isEmpty(currentConfiguration.getForEnvironment(env))) {
				textsTable.get(env).setText(currentConfiguration.getForEnvironment(env));
			} else {
				textsTable.get(env).setText("");
			}
			
		}
		
	}

	public MuleEnvironmentsEditor getEditor() {
		return editor;
	}

	public void setEditor(MuleEnvironmentsEditor editor) {
		this.editor = editor;
	}
	
	
	
}
