package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.annotation.ClassPicker;
import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "string")
public class StringEditor extends BaseStringEditor {

    // This 3 fields are only because of CXF editor
    private String max;
    private String min;
    private String step;

    private Integer setHeight;
    private String prompt;
    // TODO: Studio Typo
    private Boolean singeLineForExpressions;
    private Boolean acceptsEmptyValue;

    @ClassPicker
    private String classAttribute;

    @XmlAttribute
    public Boolean getSingeLineForExpressions() {
        return singeLineForExpressions;
    }

    public void setSingeLineForExpressions(Boolean singeLineForExpressions) {
        this.singeLineForExpressions = singeLineForExpressions;
    }

    @XmlAttribute
    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public String toString() {
        return "StringEditor [defaultValue=" + getDefaultValue() + ", prompt=" + prompt + ", singeLineForExpressions=" + singeLineForExpressions + ", getName()=" + getName()
                + ", getCaption()=" + getCaption() + "]";
    }

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

    @XmlAttribute
    public Boolean getAcceptsEmptyValue() {
        return acceptsEmptyValue;
    }

    public void setAcceptsEmptyValue(Boolean acceptsEmptyValue) {
        this.acceptsEmptyValue = acceptsEmptyValue;
    }

    @XmlAttribute(name = "class")
    public String getClassAttribute() {
        return classAttribute;
    }

    public void setClassAttribute(String classAttribute) {
        this.classAttribute = classAttribute;
    }

    @XmlAttribute
    public Integer getSetHeight() {
        return setHeight;
    }

    public void setSetHeight(Integer setHeight) {
        this.setHeight = setHeight;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((acceptsEmptyValue == null) ? 0 : acceptsEmptyValue.hashCode());
        result = prime * result + ((classAttribute == null) ? 0 : classAttribute.hashCode());
        result = prime * result + ((max == null) ? 0 : max.hashCode());
        result = prime * result + ((min == null) ? 0 : min.hashCode());
        result = prime * result + ((prompt == null) ? 0 : prompt.hashCode());
        result = prime * result + ((setHeight == null) ? 0 : setHeight.hashCode());
        result = prime * result + ((singeLineForExpressions == null) ? 0 : singeLineForExpressions.hashCode());
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
        StringEditor other = (StringEditor) obj;
        if (acceptsEmptyValue == null) {
            if (other.acceptsEmptyValue != null)
                return false;
        } else if (!acceptsEmptyValue.equals(other.acceptsEmptyValue))
            return false;
        if (classAttribute == null) {
            if (other.classAttribute != null)
                return false;
        } else if (!classAttribute.equals(other.classAttribute))
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
        if (prompt == null) {
            if (other.prompt != null)
                return false;
        } else if (!prompt.equals(other.prompt))
            return false;
        if (setHeight == null) {
            if (other.setHeight != null)
                return false;
        } else if (!setHeight.equals(other.setHeight))
            return false;
        if (singeLineForExpressions == null) {
            if (other.singeLineForExpressions != null)
                return false;
        } else if (!singeLineForExpressions.equals(other.singeLineForExpressions))
            return false;
        if (step == null) {
            if (other.step != null)
                return false;
        } else if (!step.equals(other.step))
            return false;
        return true;
    }
}
