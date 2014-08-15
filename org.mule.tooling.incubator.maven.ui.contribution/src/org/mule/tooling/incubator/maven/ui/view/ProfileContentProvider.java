package org.mule.tooling.incubator.maven.ui.view;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ProfileContentProvider implements IStructuredContentProvider {

    public Object[] getElements(Object parent) {
        return ((Configuration) (parent)).getProfiles().toArray();
    }

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
}
