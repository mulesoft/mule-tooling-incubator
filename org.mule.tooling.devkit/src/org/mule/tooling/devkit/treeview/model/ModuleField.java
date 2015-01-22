package org.mule.tooling.devkit.treeview.model;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.devkit.DevkitImages;

public class ModuleField extends DefaultNodeItem {

    public ModuleField(NodeItem parent, ICompilationUnit cu, ASTNode node) {
        super(parent, cu, node);
    }

    private FieldDeclaration field;

    @Override
    public String getLabel() {
        Object o = field.fragments().get(0);
        if (o instanceof VariableDeclarationFragment) {
            return ((VariableDeclarationFragment) o).getName().toString() + ": " + field.getType().toString();
        }
        return field.getType().toString();
    }

    @Override
    public Image getImage() {
        return DevkitImages.getManagedImage("", "configurable.gif");
    }

    public FieldDeclaration getField() {
        return field;
    }

    public void setField(FieldDeclaration field) {
        this.field = field;
    }

}
