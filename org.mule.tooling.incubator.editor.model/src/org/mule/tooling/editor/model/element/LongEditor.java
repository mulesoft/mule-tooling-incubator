package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "long")
public class LongEditor extends BaseFieldEditorElement {

    private Long min;
    private Long max;
    private Long step;
    private Long defaultValue;

    @XmlAttribute
    public Long getMin() {
        return min;
    }

    public void setMin(Long min) {
        this.min = min;
    }

    @XmlAttribute
    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    @XmlAttribute
    public Long getStep() {
        return step;
    }

    public void setStep(Long step) {
        this.step = step;
    }

    @XmlAttribute
    public Long getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Long defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "LongEditor [min=" + min + ", max=" + max + ", step=" + step + ", defaultValue=" + defaultValue + ", getName()=" + getName() + ", getCaption()=" + getCaption()
                + "]";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((max == null) ? 0 : max.hashCode());
        result = prime * result + ((min == null) ? 0 : min.hashCode());
        result = prime * result + ((step == null) ? 0 : step.hashCode());
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
        LongEditor other = (LongEditor) obj;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (max == null) {
            if (other.max != null)
                return false;
        } else if (!max.equals(other.max))
            return false;
        if (min == null) {
            if (other.min != null)
                return false;
        } else if (!min.equals(other.min))
            return false;
        if (step == null) {
            if (other.step != null)
                return false;
        } else if (!step.equals(other.step))
            return false;
        return true;
    }
}
