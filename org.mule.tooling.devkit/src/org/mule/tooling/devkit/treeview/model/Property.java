package org.mule.tooling.devkit.treeview.model;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.devkit.DevkitImages;

public class Property extends DefaultNodeItem {

    public Property(NodeItem parent, ICompilationUnit cu, ASTNode node) {
        super(parent, cu, node);
    }

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Property other = (Property) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return this.getName() + ": " + this.getValue();
    }

    @Override
    public String getLabel() {
        return getName() + ": " + getValue();
    }

    @Override
    public Image getImage() {
        return DevkitImages.getManagedImage("", "default.gif");
    }
}
