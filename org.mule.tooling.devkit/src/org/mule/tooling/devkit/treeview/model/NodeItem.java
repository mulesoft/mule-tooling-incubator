package org.mule.tooling.devkit.treeview.model;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.swt.graphics.Image;

public interface NodeItem {

	ICompilationUnit getCompilationUnit();
	
	IJavaElement getJavaElement();
	
	String getLabel();

	Image getImage();

	Object[] getChildren();

	Object getParent();
}
