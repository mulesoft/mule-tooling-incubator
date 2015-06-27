package org.mule.tooling.incubator.gradle.jobs;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.incubator.gradle.views.TestsReportView;

public class RunUnitTestsJob extends GradleBuildJob {
	
	private static final String TEST_REPORT_BROWSER_ID = "org.mule.tooling.incubator.gradle.reportBrowser";
	private IWorkbenchPage displayResultsPage;
	
	public RunUnitTestsJob(IProject project, IWorkbenchPage page) {
		super("Run Unit Tests", project, "clean", "test");
		this.displayResultsPage = page;
	}

	@Override
	protected void handleException(Exception ex) {
		
		//log the error.
		MuleCorePlugin.logError("Running unit tests has failed", ex);
		
		Throwable t = ExceptionUtils.getRootCause(ex);
		
		//open the browser with the reports.
		if (!t.getMessage().contains("file://")) {
			//TODO - display feedback somehow.
			return;
		}
		String message = t.getMessage();
		int position = message.indexOf("file://");
		final String url = message.substring(position);
		
		try {
			
			Display.getDefault().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					try {
						TestsReportView view = (TestsReportView) displayResultsPage.showView(TestsReportView.GRADLE_TEST_RESULTS_ID);
						view.openUrl(url);
					} catch (Exception e) {
						MuleCorePlugin.logError("Could not create browser", e);
					}
				}
			});
		} catch (Exception e) {
			MuleCorePlugin.logError("Could not create browser", e);
		}
		
	}
	
	@Override
	protected void handleCompletion() {
		displayTestsView();
	}
	
	private void displayTestsView() {
		try {
			
			String reportFile = null;
			IFile rf = project.getFile("build/reports/tests/index.html");
			reportFile = rf.getLocationURI().toString();
			
			final String url = reportFile; 
			
			Display.getDefault().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					try {
						TestsReportView view = (TestsReportView) displayResultsPage.showView(TestsReportView.GRADLE_TEST_RESULTS_ID);
						view.openUrl(url);
					} catch (Exception e) {
						MuleCorePlugin.logError("Could not create browser", e);
					}
				}
			});
		} catch (Exception e) {
			MuleCorePlugin.logError("Could not create browser", e);
		}		
	}
	
}
