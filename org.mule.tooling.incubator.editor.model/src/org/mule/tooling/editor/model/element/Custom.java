package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement
public class Custom extends BaseFieldEditorElement {

    private String className;
    private String javaType;
    private String bottomAnchor;

    @XmlAttribute(name = "class")
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @XmlAttribute
    public String getBottomAnchor() {
        return bottomAnchor;
    }

    public void setBottomAnchor(String bottomAnchor) {
        this.bottomAnchor = bottomAnchor;
    }

    @XmlAttribute
    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    @Override
    public String toString() {
        return "Custom [className=" + className + ", getName()=" + getName() + ", getCaption()=" + getCaption() + "]";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((bottomAnchor == null) ? 0 : bottomAnchor.hashCode());
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((javaType == null) ? 0 : javaType.hashCode());
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
        Custom other = (Custom) obj;
        if (bottomAnchor == null) {
            if (other.bottomAnchor != null)
                return false;
        } else if (!bottomAnchor.equals(other.bottomAnchor))
            return false;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        if (javaType == null) {
            if (other.javaType != null)
                return false;
        } else if (!javaType.equals(other.javaType))
            return false;
        return true;
    }
}
