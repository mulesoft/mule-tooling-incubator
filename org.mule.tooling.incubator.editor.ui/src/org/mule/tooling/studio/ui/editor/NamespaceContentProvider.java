package org.mule.tooling.studio.ui.editor;

import javax.xml.bind.JAXBException;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mule.tooling.editor.model.IComponentElement;

/**
 * @param id
 * @param title
 */
public class NamespaceContentProvider implements ITreeContentProvider {

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @Override
    public Object[] getChildren(Object inputElement) {
        GetChildVisitor getChilds = new GetChildVisitor();
        if (inputElement instanceof StudioUIFormEditorInput) {
            StudioUIFormEditorInput input = (StudioUIFormEditorInput) inputElement;
            try {
                return input.getContents();
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        if (inputElement instanceof IComponentElement) {
            IComponentElement element = (IComponentElement) inputElement;
            element.accept(getChilds);
            return getChilds.getChilds().toArray();
        }

        return new Object[0];
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }
}