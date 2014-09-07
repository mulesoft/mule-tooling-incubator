package org.mule.tooling.incubator.gradle.editors;




import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.mule.tooling.incubator.gradle.GradlePluginUtils;
import org.mule.tooling.incubator.gradle.model.StudioDepencencies;
import org.mule.tooling.incubator.gradle.model.StudioDependency;
import org.mule.tooling.ui.MuleImages;


/**
 * Editor for adding dependencies to the build.
 * @author juancavallotti
 */
public class MuleGradleDependenciesEditor extends FormPage {
	
	public static final String FORM_PAGE_ID = "org.mule.tooling.gradle.dependenciesPage";
	
	private static final String[] TABLE_HEADERS = {"Group", "Artifact", "Version"};
	
	private Table dependenciesTable;
	
	private IProject project;
	
	public MuleGradleDependenciesEditor(FormEditor editor,
			String title, IProject project) {
		super(editor, FORM_PAGE_ID, title);
		this.project = project;
	}
	
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText(getTitle());
		form.setImage(MuleImages.GLOBALS_TITLE_IMAGE);
		
		//set the layout.
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = true;
		form.getBody().setLayout(layout);
		
		//configure the toolbar buttons
		configureFormToolbarButtons(toolkit, form);
		
		//add the two sections.
		SectionPart toolBar = addButtons(toolkit, form);
		managedForm.addPart(toolBar);
		
		TableWrapData toolbarData = new TableWrapData(TableWrapData.FILL_GRAB);
		toolbarData.rowspan = 1;
		toolbarData.valign = TableWrapData.FILL;
		toolBar.getSection().setLayoutData(toolbarData);
		
		
		SectionPart depsTable = addDependenciesTable(toolkit, form);
		managedForm.addPart(depsTable);
		TableWrapData depsTableData = new TableWrapData(TableWrapData.FILL_GRAB);
		depsTableData.rowspan = 2;
		depsTableData.valign = TableWrapData.FILL;
		depsTable.getSection().setLayoutData(depsTableData);
		
		
		doRefreshTable();
	}


	private SectionPart addDependenciesTable(FormToolkit toolkit, ScrolledForm form) {
		Composite formBody = form.getBody();
		
		SectionPart part = new SectionPart(formBody, toolkit, Section.TITLE_BAR);
		
		GridLayout layout = new GridLayout(2, false);
		
		part.getSection().setText("Dependencies");
		
		Composite client = toolkit.createComposite(part.getSection());
		client.setLayout(layout);
		
		
		
		Table t = toolkit.createTable(client, SWT.BORDER | SWT.V_SCROLL);
		
		for(int colIndex = 0; colIndex < TABLE_HEADERS.length; colIndex ++) {
			TableColumn col = new TableColumn(t, SWT.NONE);
			col.setText(TABLE_HEADERS[colIndex]);
			col.setWidth(200);
		}
		
		t.setHeaderVisible(true);
		
		t.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		toolkit.paintBordersFor(client);
		part.getSection().setClient(client);
		
		dependenciesTable = t;
		return part;
	}


	private SectionPart addButtons(FormToolkit toolkit, ScrolledForm form) {
		Composite formBody = form.getBody();
		
		SectionPart part = new SectionPart(formBody, toolkit, Section.NO_TITLE);
		
		//configure the part
		part.getSection().setText("Actions");
		
		Composite client = toolkit.createComposite(part.getSection());
		RowLayout layout = new RowLayout();
		
		client.setLayout(layout);
		
		RowData buttonSizeData = new RowData();
		buttonSizeData.width = 160;
		buttonSizeData.height = 24;
		
		
		Button addButton = new Button(client, SWT.PUSH);
		addButton.setText("Add dependency...");
		addButton.setLayoutData(buttonSizeData);
		
		
		Button deleteButton = new Button(client, SWT.PUSH);
		deleteButton.setText("Remove dependency...");
		deleteButton.setLayoutData(buttonSizeData);
		
		
		toolkit.paintBordersFor(client);
		part.getSection().setClient(client);
		
		return part;
	}
	
	private void configureFormToolbarButtons(FormToolkit toolkit, ScrolledForm form) {
		
		form.getToolBarManager().add(new Action("Refresh project.") {
		});

		form.getToolBarManager().update(true);
	}
	
	
	public void populateTable(StudioDepencencies deps) {
		
		if (deps == null) {
			return;
		}
		
		dependenciesTable.removeAll();
		
		//add items
		for(StudioDependency dep : deps.getDependencies()) {
			TableItem item = new TableItem(dependenciesTable, SWT.NONE);
			item.setText(0, dep.getGroup() != null ? dep.getGroup() : "");
			item.setText(1, dep.getName() != null ? dep.getName() : "");
			item.setText(2, dep.getVersion() != null ? dep.getVersion() : "");
		}
		
	}
	
	
	private void doRefreshTable() {
		//set the data to the table.
		populateTable(GradlePluginUtils.parseStudioDependencies(project));
	}
	
}
