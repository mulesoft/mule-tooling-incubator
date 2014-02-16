package org.mule.tooling.devkit.treeview.model;

import java.util.Collections;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.devkit.DevkitImages;

public abstract class DefaultNodeItem implements NodeItem {

	private final NodeItem parent;
	protected final ICompilationUnit cu;
	protected final ASTNode node;

	public DefaultNodeItem(NodeItem parent, ICompilationUnit cu, ASTNode node) {
		this.parent = parent;
		this.cu = cu;
		this.node = node;
	}

	@Override
	public String getLabel() {
		return "n/a";
	}

	@Override
	public Image getImage() {
		return DevkitImages.getManagedImage("", "default.gif");
	}

	@Override
	public Object[] getChildren() {
		return Collections.EMPTY_LIST.toArray();
	}

	@Override
	public Object getParent() {
		return parent;
	}

	@Override
	public ICompilationUnit getCompilationUnit() {
		return cu;
	}

	@Override
	public IJavaElement getJavaElement() {
		try {
			return cu.getElementAt(node.getStartPosition());
		} catch (JavaModelException e) {
			throw new RuntimeException(e);
		}
	}
}
