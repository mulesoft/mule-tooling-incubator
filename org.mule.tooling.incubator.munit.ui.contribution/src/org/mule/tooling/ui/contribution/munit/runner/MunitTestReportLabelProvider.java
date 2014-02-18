package org.mule.tooling.ui.contribution.munit.runner;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;

public class MunitTestReportLabelProvider implements ILabelProvider {

    @Override
    public void addListener(ILabelProviderListener listener) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
    }

    @Override
    public Image getImage(Object element) {

        if (element instanceof TestStatus) {
            TestStatus testStatus = (TestStatus) element;
            if (testStatus.isFinished()) {
                if (testStatus.hasError()) {
                    return MunitPlugin.getImageDescriptor("/studio16x16-error.png").createImage();
                } else if (testStatus.hasFailed()) {
                    return MunitPlugin.getImageDescriptor("/studio16x16-failed.png").createImage();
                }

                return MunitPlugin.getImageDescriptor("/studio16x16-ok.png").createImage();
            }

        }

        return MunitPlugin.getImageDescriptor("/studio16x16.png").createImage();
    }

    @Override
    public String getText(Object element) {
        if (element instanceof TestStatus) {
            return ((TestStatus) element).getTestName();
        } else if (element instanceof SuiteStatus) {
            return ((SuiteStatus) element).getName();
        }
        return null;
    }
}
