package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "time")
public class TimeEditor extends BaseFieldEditorElement {

    private String max;
    private String min;
    private String step;

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    @XmlAttribute
    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    @XmlAttribute
    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
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
        TimeEditor other = (TimeEditor) obj;
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
