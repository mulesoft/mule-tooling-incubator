package org.mule.tooling.incubator.gradle.editors.domain;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.ide.IDE.SharedImages;
import org.mule.tooling.incubator.gradle.dialog.AddModuleDialog;
import org.mule.tooling.incubator.gradle.jobs.AddDomainModuleJob;
import org.mule.tooling.incubator.gradle.jobs.InitProjectJob;

public class DomainProjectsSectionPart extends SectionPart {
	
	private FormToolkit toolkit;
	private List<String> modules;
	private org.eclipse.swt.widgets.List modulesList; 
	private IProject currentProject;
	
	public DomainProjectsSectionPart(Composite parent, FormToolkit toolkit, List<String> modules, IProject currentProject) {
		super(parent, toolkit, Section.TITLE_BAR);
		getSection().setText("Domain Modules");
		this.toolkit = toolkit;
		
		this.modules = modules;
		if (this.modules == null) {
			modules = Collections.emptyList();
		}
		
		this.currentProject = currentProject;
		configurePanel(getSection(), toolkit);
	}

	private void configurePanel(Section section, FormToolkit toolkit2) {
		
		Composite panel = toolkit.createComposite(section);
		
		Composite toolBarPanel = toolkit.createComposite(panel);
		Composite listPanel = toolkit.createComposite(panel);
		
		GridLayoutFactory.swtDefaults().equalWidth(true).applyTo(panel);
		panel.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		toolBarPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		listPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		toolBarPanel.setLayout(new RowLayout());
		listPanel.setLayout(new FillLayout());
		
		createToolbar(toolBarPanel);
		createList(listPanel);
		
		toolkit.paintBordersFor(panel);
		section.setClient(panel);
	}

	private void createList(Composite panel) {
		modulesList = new org.eclipse.swt.widgets.List(panel, SWT.BORDER | SWT.SINGLE);
		refreshItems();
	}

	private void refreshItems() {
		modulesList.setItems(modules.toArray(new String[0]));
	}

	private void createToolbar(Composite panel) {
		ToolBar toolbar = new ToolBar(panel, SWT.HORIZONTAL | SWT.RIGHT);
		
		
		ToolItem addModuleItem = new ToolItem(toolbar, SWT.PUSH);
		addModuleItem.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_ADD));
		addModuleItem.setText("Add Module");
		addModuleItem.setToolTipText("Add new module to this Domain");
		addModuleItem.addSelectionListener(new CreateModuleListener());
		
		ToolItem createStructureItem = new ToolItem(toolbar, SWT.PUSH);
		createStructureItem.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(SharedImages.IMG_OBJ_PROJECT));
		createStructureItem.setText("Create Structure");
		createStructureItem.setToolTipText("Create the initial structure of this project");
		createStructureItem.addSelectionListener(new CreateStructureListener());
		
		ToolItem updateModuleItem = new ToolItem(toolbar, SWT.PUSH);
		updateModuleItem.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_ELCL_SYNCED));
		updateModuleItem.setText("Refresh");
		updateModuleItem.setToolTipText("Rebuilds the Project's metadata for Studio");
		
		
	}

	public List<String> getModules() {
		return modules;
	}

	public void setModules(List<String> modules) {
		this.modules = modules;
	}
	
	private class CreateStructureListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			
			if (modulesList.getSelectionCount() == 0) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Please select a subproject in the list to run this action.");
				return;
			}
			
			String value = modulesList.getSelection()[0];
			
			if (ResourcesPlugin.getWorkspace().getRoot().getProject(value).exists()) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "There is already a project with the name: " + value + " in the workspace.");
				return;
			}
			
			InitProjectJob job = new InitProjectJob(currentProject, value);
			
			job.doSchedule();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			
		}
		
	}
	
	private class CreateModuleListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			
			AddModuleDialog dialog = new AddModuleDialog(Display.getDefault().getActiveShell(), modules);			
			int retCode = dialog.open();
			
			if (retCode == AddModuleDialog.CANCEL || !dialog.isFormValid()) {
				return;
			}
			
			new AddDomainModuleJob(dialog.getDialogInput(), currentProject).schedule();
			
			modules.add(dialog.getDialogInput());
			refreshItems();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			
			
		}
		
	}
	
}
