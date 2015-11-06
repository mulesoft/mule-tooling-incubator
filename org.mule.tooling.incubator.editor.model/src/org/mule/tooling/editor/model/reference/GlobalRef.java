package org.mule.tooling.editor.model.reference;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement
public class GlobalRef extends AbstractRef {

    private String attrName;
    private String requiredType;
    private String additionalCheckbox;
    private Boolean hideToolBar;// TODO should this be in the parent...it is only used here...but...

    @Override
    public String toString() {
        return "GlobalRef [getName()=" + getName() + ", getCaption()=" + getCaption() + ", getDescription()=" + getDescription() + "]";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getRequiredType() {
        return requiredType;
    }

    public void setRequiredType(String requiredType) {
        this.requiredType = requiredType;
    }

    @XmlAttribute
    public String getAdditionalCheckbox() {
        return additionalCheckbox;
    }

    public void setAdditionalCheckbox(String additionalCheckbox) {
        this.additionalCheckbox = additionalCheckbox;
    }

    @XmlAttribute
    public Boolean getHideToolBar() {
        return hideToolBar;
    }

    public void setHideToolBar(Boolean hideToolBar) {
        this.hideToolBar = hideToolBar;
    }
    
    @XmlAttribute
    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((additionalCheckbox == null) ? 0 : additionalCheckbox.hashCode());
        result = prime * result + ((attrName == null) ? 0 : attrName.hashCode());
        result = prime * result + ((hideToolBar == null) ? 0 : hideToolBar.hashCode());
        result = prime * result + ((requiredType == null) ? 0 : requiredType.hashCode());
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
        GlobalRef other = (GlobalRef) obj;
        if (additionalCheckbox == null) {
            if (other.additionalCheckbox != null)
                return false;
        } else if (!additionalCheckbox.equals(other.additionalCheckbox))
            return false;
        if (attrName == null) {
            if (other.attrName != null)
                return false;
        } else if (!attrName.equals(other.attrName))
            return false;
        if (hideToolBar == null) {
            if (other.hideToolBar != null)
                return false;
        } else if (!hideToolBar.equals(other.hideToolBar))
            return false;
        if (requiredType == null) {
            if (other.requiredType != null)
                return false;
        } else if (!requiredType.equals(other.requiredType))
            return false;
        return true;
    }
}
