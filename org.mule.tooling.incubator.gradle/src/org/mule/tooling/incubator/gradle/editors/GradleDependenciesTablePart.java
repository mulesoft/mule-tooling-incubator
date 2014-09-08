package org.mule.tooling.incubator.gradle.editors;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.mule.tooling.incubator.gradle.GradlePluginUtils;
import org.mule.tooling.incubator.gradle.model.StudioDependencies;
import org.mule.tooling.incubator.gradle.model.StudioDependency;

public class GradleDependenciesTablePart extends SectionPart {

	private static final String[] TABLE_HEADERS = {"Group", "Artifact", "Version"};
	
	private Table table;
	
	private IProject project;
	
	private List<StudioDependency> currentModel;
	
	public GradleDependenciesTablePart(Composite parent, FormToolkit toolkit, IProject currentProject) {
		super(parent, toolkit, Section.TITLE_BAR);
		this.project = currentProject;
		configureComponent(getSection(), toolkit);
	}
	
	
	protected void configureComponent(Section section, FormToolkit toolkit) {
		GridLayout layout = new GridLayout(2, false);
		section.setText("Dependencies");
		Composite client = toolkit.createComposite(section);
		client.setLayout(layout);
		
		table = toolkit.createTable(client, SWT.BORDER | SWT.V_SCROLL);
		
		for(int colIndex = 0; colIndex < TABLE_HEADERS.length; colIndex ++) {
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText(TABLE_HEADERS[colIndex]);
			col.setWidth(200);
		}
		
		table.setHeaderVisible(true);
		
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		toolkit.paintBordersFor(client);
		section.setClient(client);
		
		
	}

	public void populateTable(StudioDependencies deps) {
		
		if (deps == null || deps.getDependencies() == null) {
			currentModel = new ArrayList<StudioDependency>();
			return;
		}
		
		table.removeAll();
		
		currentModel = deps.getDependencies();
		
		//add items
		for(StudioDependency dep : deps.getDependencies()) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, dep.getGroup() != null ? dep.getGroup() : "");
			item.setText(1, dep.getName() != null ? dep.getName() : "");
			item.setText(2, dep.getVersion() != null ? dep.getVersion() : "");
		}
		
	}
	
	
	public void refreshData() {
		//set the data to the table.
		populateTable(GradlePluginUtils.parseStudioDependencies(project));
	}
	
	public void removeSelectedRow() {
		
		int item = table.getSelectionIndex();
		
		if (item != -1) {
			currentModel.remove(item);
		}
		
		//save model
		saveCurrentModel();
		populateTable(new StudioDependencies(currentModel));
	}
	
	public void addNewRow() {
		
		StudioDependency data = new StudioDependency();
		DependencyEditorDialog dialog = new DependencyEditorDialog(getSection().getShell(), data);
		int result = dialog.open();
		
		if (result != DependencyEditorDialog.OK) {
			return;
		}
		
		currentModel.add(data);
		populateTable(new StudioDependencies(currentModel));
		saveCurrentModel();
	}
	
	private void saveCurrentModel() {
		GradlePluginUtils.saveStudioDependencies(project, new StudioDependencies(currentModel));
	}

}
