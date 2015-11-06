package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "password")
public class PasswordEditor extends BaseFieldEditorElement {
    
    private Boolean alwaysFillSimple;
    
    @Override
    public String toString() {
        return "PasswordEditor [getName()=" + getName() + ", getCaption()=" + getCaption() + "]";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public Boolean getAlwaysFillSimple() {
        return alwaysFillSimple;
    }

    public void setAlwaysFillSimple(Boolean alwaysFillSimple) {
        this.alwaysFillSimple = alwaysFillSimple;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((alwaysFillSimple == null) ? 0 : alwaysFillSimple.hashCode());
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
        PasswordEditor other = (PasswordEditor) obj;
        if (alwaysFillSimple == null) {
            if (other.alwaysFillSimple != null)
                return false;
        } else if (!alwaysFillSimple.equals(other.alwaysFillSimple))
            return false;
        return true;
    }
}
