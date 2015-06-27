package org.mule.tooling.incubator.gradle.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class TestsReportView extends ViewPart {
	
	public static final String GRADLE_TEST_RESULTS_ID = "org.mule.tooling.incubator.gradle.reportBrowser";
	private Browser browser;
	
	@Override
	public void createPartControl(Composite parent) {		
		this.browser = new Browser(parent, SWT.WEBKIT);	
	}
	
	@Override
	public void setFocus() {
	}

	public void openUrl(String url) {
		browser.setUrl(url);
	}

}
