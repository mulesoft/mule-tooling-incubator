package org.mule.tooling.editor.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.element.BaseFieldEditorElement;
import org.mule.tooling.editor.model.element.Option;

@XmlRootElement
public class Radio extends BaseFieldEditorElement {

    private String defaultValue;
    private Boolean horizontal;

    private List<Option> options;

    @XmlElementRef
    public List<Option> getOptions() {
        if (options == null) {
            options = new ArrayList<Option>();
        }
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @XmlAttribute
    public Boolean getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(Boolean horizontal) {
        this.horizontal = horizontal;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((horizontal == null) ? 0 : horizontal.hashCode());
        result = prime * result + ((options == null) ? 0 : options.hashCode());
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
        Radio other = (Radio) obj;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (horizontal == null) {
            if (other.horizontal != null)
                return false;
        } else if (!horizontal.equals(other.horizontal))
            return false;
        if (options == null) {
            if (other.options != null)
                return false;
        } else if (!options.equals(other.options))
            return false;
        return true;
    }
}
