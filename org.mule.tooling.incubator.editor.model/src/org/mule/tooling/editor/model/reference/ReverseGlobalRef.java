package org.mule.tooling.editor.model.reference;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement
public class ReverseGlobalRef extends AbstractRef {

    private String cells;
    private String fieldWithLink;
    private String fieldWithParentName;
    private String childrenType;
    private String parentType;

    @Override
    public String toString() {
        return "ReverseGlobalRef [getName()=" + getName() + ", getCaption()=" + getCaption() + "]";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getCells() {
        return cells;
    }

    public void setCells(String cells) {
        this.cells = cells;
    }

    @XmlAttribute
    public String getFieldWithLink() {
        return fieldWithLink;
    }

    public void setFieldWithLink(String fieldWithLink) {
        this.fieldWithLink = fieldWithLink;
    }

    @XmlAttribute
    public String getFieldWithParentName() {
        return fieldWithParentName;
    }

    public void setFieldWithParentName(String fieldWithParentName) {
        this.fieldWithParentName = fieldWithParentName;
    }

    @XmlAttribute
    public String getChildrenType() {
        return childrenType;
    }

    public void setChildrenType(String childrenType) {
        this.childrenType = childrenType;
    }

    @XmlAttribute
    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((cells == null) ? 0 : cells.hashCode());
        result = prime * result + ((childrenType == null) ? 0 : childrenType.hashCode());
        result = prime * result + ((fieldWithLink == null) ? 0 : fieldWithLink.hashCode());
        result = prime * result + ((fieldWithParentName == null) ? 0 : fieldWithParentName.hashCode());
        result = prime * result + ((parentType == null) ? 0 : parentType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReverseGlobalRef other = (ReverseGlobalRef) obj;
        if (cells == null) {
            if (other.cells != null)
                return false;
        } else if (!cells.equals(other.cells))
            return false;
        if (childrenType == null) {
            if (other.childrenType != null)
                return false;
        } else if (!childrenType.equals(other.childrenType))
            return false;
        if (fieldWithLink == null) {
            if (other.fieldWithLink != null)
                return false;
        } else if (!fieldWithLink.equals(other.fieldWithLink))
            return false;
        if (fieldWithParentName == null) {
            if (other.fieldWithParentName != null)
                return false;
        } else if (!fieldWithParentName.equals(other.fieldWithParentName))
            return false;
        if (parentType == null) {
            if (other.parentType != null)
                return false;
        } else if (!parentType.equals(other.parentType))
            return false;
        return true;
    }
}
