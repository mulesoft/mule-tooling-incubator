package org.mule.tooling.incubator.utils.environments.editor;

import java.util.HashMap;
import java.util.List;

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
import org.mule.tooling.incubator.utils.environments.model.EnvironmentSettingElement;

public class MuleEnvironmentConfigsPart extends SectionPart {
	
	private FormToolkit toolkit;
	
	private List<EnvironmentSettingElement> currentConfiguration;
	private HashMap<String, Text> textsTable;
	private MuleEnvironmentsEditor editor;
	
	public MuleEnvironmentConfigsPart(Composite parent, FormToolkit toolkit, List<EnvironmentSettingElement> initialConfiguration) {
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
		
		for(EnvironmentSettingElement elm : currentConfiguration) {
			
			Section sectionPanel = toolkit.createSection(globalPanel, Section.TITLE_BAR);
			Composite sectionContent = toolkit.createComposite(sectionPanel);
			
			final String environment = elm.getEnvironment();
			
			sectionPanel.setText(elm.getEnvironment());
			
			GridLayout gl = new GridLayout();
			gl.numColumns = 2;
			
			sectionContent.setLayout(gl);
			
			toolkit.createLabel(sectionContent, "Value:", SWT.BOLD);
			final Text text = toolkit.createText(sectionContent, elm.getValue());
			text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			toolkit.paintBordersFor(sectionContent);
			sectionPanel.setClient(sectionContent);
			
			text.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					for(EnvironmentSettingElement config : currentConfiguration) {
						if (config.getEnvironment().equals(environment)) {
							String originalValue = config.getValue();
							String newValue = ((Text)e.getSource()).getText();
							
							if (!originalValue.equals(newValue)) {
								config.setValue(newValue);
								editor.setDirty(true);
							}
						}
					}
				}
			});
			
			
			textsTable.put(elm.getEnvironment(), text);
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

	public List<EnvironmentSettingElement> getCurrentConfiguration() {
		
		for(EnvironmentSettingElement elm : currentConfiguration) {
			elm.setValue(textsTable.get(elm.getEnvironment()).getText());
		}
		
		return currentConfiguration;
	}

	public void setCurrentConfiguration(
			List<EnvironmentSettingElement> currentConfiguration) {

		this.currentConfiguration = currentConfiguration;
		
		for(EnvironmentSettingElement config : currentConfiguration) {
			if (!textsTable.containsKey(config.getEnvironment())) {
				//this is a different set of environments!!
				System.out.println("Had to repaint!!");
				repaintPanels();
				break;
			}
			
			textsTable.get(config.getEnvironment()).setText(config.getValue());
			
		}
		
	}

	public MuleEnvironmentsEditor getEditor() {
		return editor;
	}

	public void setEditor(MuleEnvironmentsEditor editor) {
		this.editor = editor;
	}
	
	
	
}
