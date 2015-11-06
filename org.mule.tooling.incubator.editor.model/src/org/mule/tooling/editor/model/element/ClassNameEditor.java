package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.annotation.ClassPicker;
import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "classname")
public class ClassNameEditor extends BaseFieldEditorElement {

    @ClassPicker
    private String implementsClass;
    private Boolean isSupportsImport;// TODO PARA QUE?

    @ClassPicker
    private String extendsClass;

    @XmlAttribute(name = "implements")
    public String getImplementsClass() {
        return implementsClass;
    }

    public void setImplementsClass(String implementsClass) {
        this.implementsClass = implementsClass;
    }

    @XmlAttribute(name = "extends")
    public String getExtendsClass() {
        return extendsClass;
    }

    public void setExtendsClass(String extendsClass) {
        this.extendsClass = extendsClass;
    }

    @Override
    public String toString() {
        return "ClassNameEditor [ getName()=" + getName() + ", getDescription()=" + getDescription() + "]";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public Boolean getIsSupportsImport() {
        return isSupportsImport;
    }

    public void setIsSupportsImport(Boolean isSupportsImport) {
        this.isSupportsImport = isSupportsImport;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((extendsClass == null) ? 0 : extendsClass.hashCode());
        result = prime * result + ((implementsClass == null) ? 0 : implementsClass.hashCode());
        result = prime * result + ((isSupportsImport == null) ? 0 : isSupportsImport.hashCode());
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
        ClassNameEditor other = (ClassNameEditor) obj;
        if (extendsClass == null) {
            if (other.extendsClass != null)
                return false;
        } else if (!extendsClass.equals(other.extendsClass))
            return false;
        if (implementsClass == null) {
            if (other.implementsClass != null)
                return false;
        } else if (!implementsClass.equals(other.implementsClass))
            return false;
        if (isSupportsImport == null) {
            if (other.isSupportsImport != null)
                return false;
        } else if (!isSupportsImport.equals(other.isSupportsImport))
            return false;
        return true;
    }
}
