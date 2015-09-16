package org.mule.tooling.devkit.treeview.model;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.devkit.DevkitImages;

public class MetaDataCategory extends Module {

    public MetaDataCategory(NodeItem parent, ICompilationUnit cu, ASTNode node) {
        super(parent, cu, node);
    }

    @Override
    public Image getImage() {
        return DevkitImages.getManagedImage("", "metadata_category.png");
    }
}
