package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement
public class SoapInterceptor extends BaseFieldEditorElement {

    private Boolean allowMultiple;
    private Boolean inplace;

    @XmlAttribute
    public Boolean getAllowMultiple() {
        return allowMultiple;
    }

    public void setAllowMultiple(Boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }

    @XmlAttribute
    public Boolean getInplace() {
        return inplace;
    }

    public void setInplace(Boolean inplace) {
        this.inplace = inplace;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((allowMultiple == null) ? 0 : allowMultiple.hashCode());
        result = prime * result + ((inplace == null) ? 0 : inplace.hashCode());
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
        SoapInterceptor other = (SoapInterceptor) obj;
        if (allowMultiple == null) {
            if (other.allowMultiple != null)
                return false;
        } else if (!allowMultiple.equals(other.allowMultiple))
            return false;
        if (inplace == null) {
            if (other.inplace != null)
                return false;
        } else if (!inplace.equals(other.inplace))
            return false;
        return true;
    }
}
