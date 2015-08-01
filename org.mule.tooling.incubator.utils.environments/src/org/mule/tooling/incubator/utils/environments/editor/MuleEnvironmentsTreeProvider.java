package org.mule.tooling.incubator.utils.environments.editor;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.incubator.utils.environments.model.PropertyKeyTreeNode;

public class MuleEnvironmentsTreeProvider implements ITreeContentProvider, ILabelProvider {

	@Override
	public void dispose() {
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		
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
		
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
	}

	@Override
	public String getText(Object element) {
		PropertyKeyTreeNode node = (PropertyKeyTreeNode) element;		
		return node.getKeyFragment();
	}

	@Override
	public Object[] getElements(Object inputElement) {
		PropertyKeyTreeNode node = (PropertyKeyTreeNode) inputElement;
		return node.getChildrenArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		PropertyKeyTreeNode node = (PropertyKeyTreeNode) parentElement;
		return node.getChildrenArray();
	}

	@Override
	public Object getParent(Object element) {
		PropertyKeyTreeNode node = (PropertyKeyTreeNode) element;
		return node.getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		PropertyKeyTreeNode node = (PropertyKeyTreeNode) element;
		return !node.getChildren().isEmpty();
	}

}
