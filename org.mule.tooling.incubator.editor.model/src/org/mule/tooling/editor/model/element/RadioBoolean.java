package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.annotation.ClassPicker;
import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement
public class RadioBoolean extends BaseFieldEditorElement {

    private String group;
    private Boolean defaultValue;
    private Integer margin;
    @ClassPicker(mustImplement = org.mule.tooling.ui.modules.core.widgets.editors.ILoadedValueModifier.class)
    private String loadedValueModifier;

    @Override
    public String toString() {
        return "RadioBoolean [getName()=" + getName() + ", getCaption()=" + getCaption() + "]";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @XmlAttribute
    public Boolean getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    @XmlAttribute
    public Integer getMargin() {
        return margin;
    }

    public void setMargin(Integer margin) {
        this.margin = margin;
    }

    @XmlAttribute
    public String getLoadedValueModifier() {
        return loadedValueModifier;
    }

    public void setLoadedValueModifier(String loadedValueModifier) {
        this.loadedValueModifier = loadedValueModifier;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((group == null) ? 0 : group.hashCode());
        result = prime * result + ((loadedValueModifier == null) ? 0 : loadedValueModifier.hashCode());
        result = prime * result + ((margin == null) ? 0 : margin.hashCode());
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
        RadioBoolean other = (RadioBoolean) obj;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (group == null) {
            if (other.group != null)
                return false;
        } else if (!group.equals(other.group))
            return false;
        if (loadedValueModifier == null) {
            if (other.loadedValueModifier != null)
                return false;
        } else if (!loadedValueModifier.equals(other.loadedValueModifier))
            return false;
        if (margin == null) {
            if (other.margin != null)
                return false;
        } else if (!margin.equals(other.margin))
            return false;
        return true;
    }
}
