package org.mule.tooling.incubator.maven.ui.view;

import org.apache.maven.model.Dependency;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mule.tooling.incubator.maven.core.TreeNode;

public class DependencyTreeContentProvider implements ITreeContentProvider {

    private TreeNode<Dependency> root;
    private String section = "Dependencies";

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        this.root = (TreeNode<Dependency>) newInput;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement.equals(section)) {
            return new Object[] { root };
        }else {
            return ((TreeNode<Dependency>) parentElement).getChildItems().toArray();
        }
    }

    @Override
    public Object getParent(Object element) {

        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return this.getChildren(element) != null && this.getChildren(element).length > 0;
    }
}
