package org.mule.tooling.incubator.gradle.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE.SharedImages;
import org.eclipse.ui.part.ViewPart;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.utils.CoreUtils;
import org.mule.tooling.incubator.gradle.GradlePluginUtils;
import org.mule.tooling.incubator.gradle.jobs.RunUnitTestsJob;

public class TestsReportView extends ViewPart implements ISelectionListener {
	
	public static final String GRADLE_TEST_RESULTS_ID = "org.mule.tooling.incubator.gradle.reportBrowser";
	private Browser browser;
	private Action rerunAction;
	private IProject currentSelectedProject;
	private IWorkbenchPage page;
	
	@Override
	public void createPartControl(Composite parent) {		
		this.browser = new Browser(parent, SWT.WEBKIT);
		contributeToolbar();
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
	}
	
	@Override
	public void setFocus() {
	}

	public void openUrl(String url) {
		browser.setUrl(url);
	}

	private void contributeToolbar() {
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager tbm = bars.getToolBarManager();
		
		rerunAction = new Action("Run or Re-Run unit tests...") {
			@Override
			public void run() {
				runTestsMaybe();
			}
		};

		rerunAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(SharedImages.IMG_OBJS_TASK_TSK));
		tbm.add(rerunAction);
	}
	
	/**
	 * Runs the thests only if the current project is a gradle project.
	 */
	public void runTestsMaybe() {
		if (currentSelectedProject == null) {
			return;
		}
		
		RunUnitTestsJob job = new RunUnitTestsJob(currentSelectedProject, page);
		job.doSchedule();
		
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		
		try {
		
			currentSelectedProject = null;
			
			if (!(selection instanceof IStructuredSelection)) {
				return;
			}
			
			IMuleProject project = getMuleProjectFromSelection(selection);
			
			if (project == null) {
				return;
			}
			
			currentSelectedProject = project.getProjectFile().getProject();
			page = part.getSite().getPage();
		} finally {
			rerunAction.setEnabled(currentSelectedProject != null && GradlePluginUtils.shallowCheckIsGradleproject(currentSelectedProject));
		}
	}

	private IMuleProject getMuleProjectFromSelection(ISelection selection) {
		try {
			return CoreUtils.getMuleProjectForSelection((IStructuredSelection) selection);
		} catch (CoreException ex) {
			//really not important
			return null;
		}
	}
	
	@Override
    public void dispose() {
        getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
    }
	
}
