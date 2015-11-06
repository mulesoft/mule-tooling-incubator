package org.mule.tooling.editor.model.reference;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.mule.tooling.editor.model.LocalRef;
import org.mule.tooling.editor.model.element.BaseFieldEditorElement;

@XmlRootElement
@XmlSeeAlso({ ContainerRef.class, FlowRef.class, GlobalRef.class, LocalRef.class, ReverseGlobalRef.class })
public abstract class AbstractRef extends BaseFieldEditorElement {

    private Boolean removeEmptyOption;
    private String defaultValue;
    private Boolean allowsCustom;// TODO Solo se usa en GLobalRef...pero puede tener sentido aca

    @XmlAttribute
    public Boolean getRemoveEmptyOption() {
        return removeEmptyOption;
    }

    public void setRemoveEmptyOption(Boolean removeEmptyOption) {
        this.removeEmptyOption = removeEmptyOption;
    }

    @XmlAttribute
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @XmlAttribute
    public Boolean getAllowsCustom() {
        return allowsCustom;
    }

    public void setAllowsCustom(Boolean allowsCustom) {
        this.allowsCustom = allowsCustom;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((allowsCustom == null) ? 0 : allowsCustom.hashCode());
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((removeEmptyOption == null) ? 0 : removeEmptyOption.hashCode());
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
        AbstractRef other = (AbstractRef) obj;
        if (allowsCustom == null) {
            if (other.allowsCustom != null)
                return false;
        } else if (!allowsCustom.equals(other.allowsCustom))
            return false;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (removeEmptyOption == null) {
            if (other.removeEmptyOption != null)
                return false;
        } else if (!removeEmptyOption.equals(other.removeEmptyOption))
            return false;
        return true;
    }

}
