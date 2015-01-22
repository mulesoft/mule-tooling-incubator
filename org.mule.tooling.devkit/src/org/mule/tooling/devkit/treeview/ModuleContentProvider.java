package org.mule.tooling.devkit.treeview;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mule.tooling.devkit.treeview.model.Module;
import org.mule.tooling.devkit.treeview.model.NodeItem;
import org.mule.tooling.devkit.treeview.model.ProjectRoot;

public class ModuleContentProvider implements ITreeContentProvider {

    private ProjectRoot modules;

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.modules = (ProjectRoot) newInput;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        return ((NodeItem) parentElement).getChildren();
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof Module) {
            return modules;
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return this.getChildren(element).length > 0;
    }

}
