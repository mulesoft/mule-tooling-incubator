package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "name")
public class NameEditor extends BaseFieldEditorElement {

    private String defaultValue;
    // TODO xq no usar el required??
    private Boolean appearsAsRequired;

    @Override
    public String toString() {
        return "NameEditor [getName()=" + getName() + ", getCaption()=" + getCaption() + ", getDescription()=" + getDescription() + "]";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public Boolean getAppearsAsRequired() {
        return appearsAsRequired;
    }

    public void setAppearsAsRequired(Boolean appearsAsRequired) {
        this.appearsAsRequired = appearsAsRequired;
    }

    @XmlAttribute
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((appearsAsRequired == null) ? 0 : appearsAsRequired.hashCode());
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
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
        NameEditor other = (NameEditor) obj;
        if (appearsAsRequired == null) {
            if (other.appearsAsRequired != null)
                return false;
        } else if (!appearsAsRequired.equals(other.appearsAsRequired))
            return false;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        return true;
    }
}
