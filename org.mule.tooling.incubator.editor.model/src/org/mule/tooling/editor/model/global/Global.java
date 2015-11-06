package org.mule.tooling.editor.model.global;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement
public class Global extends AbstractGlobalElement {

    private String category;
    private String wrapIn;

    @XmlAttribute
    public String getWrapIn() {
        return wrapIn;
    }

    public void setWrapIn(String wrapIn) {
        this.wrapIn = wrapIn;
    }

    @XmlAttribute
    public String getCategory() {
        return category;
    }

    
    public void setCategory(String category) {
        this.category = category;
    }
    
    @Override
    public String toString() {
        return "Global (" + super.toString() + ")";
    }
    
    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((wrapIn == null) ? 0 : wrapIn.hashCode());
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
        Global other = (Global) obj;
        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        if (wrapIn == null) {
            if (other.wrapIn != null)
                return false;
        } else if (!wrapIn.equals(other.wrapIn))
            return false;
        return true;
    }
}
