package org.mule.tooling.incubator.gradle.editors;




import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.mule.tooling.incubator.gradle.jobs.SynchronizeProjectGradleBuildJob;
import org.mule.tooling.ui.MuleImages;


/**
 * Editor for adding dependencies to the build.
 * @author juancavallotti
 */
public class MuleGradleDependenciesEditor extends FormPage {
	
	public static final String FORM_PAGE_ID = "org.mule.tooling.gradle.dependenciesPage";
	
	private IProject project;
	
	private GradleDependenciesTablePart table;
	
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
		
		
		GradleDependenciesTablePart depsTable = addDependenciesTable(toolkit, form);
		managedForm.addPart(depsTable);
		TableWrapData depsTableData = new TableWrapData(TableWrapData.FILL_GRAB);
		depsTableData.rowspan = 2;
		depsTableData.valign = TableWrapData.FILL;
		depsTable.getSection().setLayoutData(depsTableData);
		
		this.table = depsTable;
		table.refreshData();
	}


	private GradleDependenciesTablePart addDependenciesTable(FormToolkit toolkit, ScrolledForm form) {
		Composite formBody = form.getBody();
		GradleDependenciesTablePart part = new GradleDependenciesTablePart(formBody, toolkit, project);
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
		addButton.addListener(SWT.Selection, new Listener(){
			
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				table.addNewRow();
			}
		});
		
		
		Button deleteButton = new Button(client, SWT.PUSH);
		deleteButton.setText("Remove dependency...");
		deleteButton.setLayoutData(buttonSizeData);
		deleteButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				table.removeSelectedRow();
			}
			
		});
		
		
		toolkit.paintBordersFor(client);
		part.getSection().setClient(client);
		
		return part;
	}
	
	private void configureFormToolbarButtons(FormToolkit toolkit, ScrolledForm form) {
		
		form.getToolBarManager().add(new Action("Refresh project.") {
			@Override
			public void runWithEvent(Event event) {
				doSynchronize();
			}
		});

		form.getToolBarManager().update(true);
	}

	
	private void doSynchronize() {
		SynchronizeProjectGradleBuildJob job = new SynchronizeProjectGradleBuildJob(project) {
			
			@Override
			protected void handleException(Exception ex) {
				// TODO Auto-generated method stub
				ex.printStackTrace();
			}
		};
		
		job.schedule();
	}
	
}
