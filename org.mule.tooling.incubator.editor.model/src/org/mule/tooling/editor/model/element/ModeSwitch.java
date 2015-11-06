package org.mule.tooling.editor.model.element;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "modeSwitch")
public class ModeSwitch extends BaseFieldEditorElement {

    private Boolean alwaysCombo;
    private Boolean changesVisibility;
    private String sort;// TODO potential enum
    private String defaultValue;
    private Boolean asRadioGroup;

    private List<AbstractMode> modes;

    @XmlElementRef
    public List<AbstractMode> getModes() {
        if (modes == null) {
            modes = new ArrayList<AbstractMode>();
        }
        return modes;
    }

    public void setModes(List<AbstractMode> modes) {
        this.modes = modes;
    }

    @XmlAttribute
    public Boolean getAlwaysCombo() {
        return alwaysCombo;
    }

    public void setAlwaysCombo(Boolean alwaysCombo) {
        this.alwaysCombo = alwaysCombo;
    }

    @XmlAttribute
    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    @XmlAttribute
    public Boolean getChangesVisibility() {
        return changesVisibility;
    }

    public void setChangesVisibility(Boolean changesVisibility) {
        this.changesVisibility = changesVisibility;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ModeSwitch [getName()=" + getName() + ", getCaption()=" + getCaption() + "]";
    }

    @XmlAttribute
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @XmlAttribute
    public Boolean getAsRadioGroup() {
        return asRadioGroup;
    }

    public void setAsRadioGroup(Boolean asRadioGroup) {
        this.asRadioGroup = asRadioGroup;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((alwaysCombo == null) ? 0 : alwaysCombo.hashCode());
        result = prime * result + ((asRadioGroup == null) ? 0 : asRadioGroup.hashCode());
        result = prime * result + ((changesVisibility == null) ? 0 : changesVisibility.hashCode());
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((modes == null) ? 0 : modes.hashCode());
        result = prime * result + ((sort == null) ? 0 : sort.hashCode());
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
        ModeSwitch other = (ModeSwitch) obj;
        if (alwaysCombo == null) {
            if (other.alwaysCombo != null)
                return false;
        } else if (!alwaysCombo.equals(other.alwaysCombo))
            return false;
        if (asRadioGroup == null) {
            if (other.asRadioGroup != null)
                return false;
        } else if (!asRadioGroup.equals(other.asRadioGroup))
            return false;
        if (changesVisibility == null) {
            if (other.changesVisibility != null)
                return false;
        } else if (!changesVisibility.equals(other.changesVisibility))
            return false;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (modes == null) {
            if (other.modes != null)
                return false;
        } else if (!modes.equals(other.modes))
            return false;
        if (sort == null) {
            if (other.sort != null)
                return false;
        } else if (!sort.equals(other.sort))
            return false;
        return true;
    }

}
