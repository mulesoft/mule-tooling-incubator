package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "boolean")
public class BooleanEditor extends BaseFieldEditorElement {

    private Boolean value;// TODO para que? si el defaultValue sobra
    private Boolean defaultAttribute;// TODO para que? si el defaultValue sobra
    private Boolean defaultValue;
    private Boolean negative;

    @XmlAttribute
    public Boolean getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public Boolean getNegative() {
        return negative;
    }

    public void setNegative(Boolean negative) {
        this.negative = negative;
    }

    @XmlAttribute(name = "default")
    public Boolean getDefaultAttribute() {
        return defaultAttribute;
    }

    public void setDefaultAttribute(Boolean defaultAttribute) {
        this.defaultAttribute = defaultAttribute;
    }

    @XmlAttribute
    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((defaultAttribute == null) ? 0 : defaultAttribute.hashCode());
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((negative == null) ? 0 : negative.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        BooleanEditor other = (BooleanEditor) obj;
        if (defaultAttribute == null) {
            if (other.defaultAttribute != null)
                return false;
        } else if (!defaultAttribute.equals(other.defaultAttribute))
            return false;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (negative == null) {
            if (other.negative != null)
                return false;
        } else if (!negative.equals(other.negative))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
}
