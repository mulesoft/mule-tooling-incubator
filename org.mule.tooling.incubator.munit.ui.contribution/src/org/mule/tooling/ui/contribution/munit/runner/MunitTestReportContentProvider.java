package org.mule.tooling.ui.contribution.munit.runner;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class MunitTestReportContentProvider implements ITreeContentProvider {

    private Action reRunAction;
    private Action reDebugAction;

    public MunitTestReportContentProvider(Action reRunAction, Action reDebugAction) {
        this.reRunAction = reRunAction;
        this.reDebugAction = reDebugAction;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        SuiteStatus status = (SuiteStatus) newInput;
        if (status != null && status.getNumberOfTests() > 0) {
            reRunAction.setEnabled(true);
            reDebugAction.setEnabled(true);
        } else {
            reRunAction.setEnabled(false);
            reDebugAction.setEnabled(false);
        }
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof SuiteStatus) {
            return ((SuiteStatus) inputElement).getTests().toArray();
        }
        return null;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        return getElements(parentElement);
    }

    @Override
    public Object getParent(Object element) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        // TODO Auto-generated method stub
        return false;
    }
}
