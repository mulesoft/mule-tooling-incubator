package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement
public class ChildElement extends BaseFieldEditorElement {

    private Boolean allowMultiple;
    private String additionalPriorities;
    // TODO este elemento esta en otra jerarquia....habra que mover la clase??
    private String localId;
    private Boolean tableUI;
    private Boolean removeBorder;
    private String groupLabel;
    private Integer xmlOrder;
    private Boolean inplace;
    private Boolean positional;

    private String allowedSubTypes;// TODO WTF?
    private String allowSubTypes;// TODO WTF?

    @Override
    public String toString() {
        return "ChildElement [getName()=" + getName() + ", getCaption()=" + getCaption() + ", getDescription()=" + getDescription() + "]";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    @XmlAttribute
    public Boolean getTableUI() {
        return tableUI;
    }

    public void setTableUI(Boolean tableUI) {
        this.tableUI = tableUI;
    }

    @XmlAttribute
    public Boolean getRemoveBorder() {
        return removeBorder;
    }

    public void setRemoveBorder(Boolean removeBorder) {
        this.removeBorder = removeBorder;
    }

    @XmlAttribute
    public String getGroupLabel() {
        return groupLabel;
    }

    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    @XmlAttribute
    public Integer getXmlOrder() {
        return xmlOrder;
    }

    public void setXmlOrder(Integer xmlOrder) {
        this.xmlOrder = xmlOrder;
    }

    @XmlAttribute
    public Boolean getInplace() {
        return inplace;
    }

    public void setInplace(Boolean inplace) {
        this.inplace = inplace;
    }

    @XmlAttribute
    public String getAdditionalPriorities() {
        return additionalPriorities;
    }

    public void setAdditionalPriorities(String additionalPriorities) {
        this.additionalPriorities = additionalPriorities;
    }

    @XmlAttribute
    public String getAllowedSubTypes() {
        return allowedSubTypes;
    }

    public void setAllowedSubTypes(String allowedSubTypes) {
        this.allowedSubTypes = allowedSubTypes;
    }

    @XmlAttribute
    public Boolean getAllowMultiple() {
        return allowMultiple;
    }

    public void setAllowMultiple(Boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }

    @XmlAttribute
    public String getAllowSubTypes() {
        return allowSubTypes;
    }

    public void setAllowSubTypes(String allowSubTypes) {
        this.allowSubTypes = allowSubTypes;
    }

    @XmlAttribute
    public Boolean getPositional() {
        return positional;
    }

    public void setPositional(Boolean positional) {
        this.positional = positional;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((additionalPriorities == null) ? 0 : additionalPriorities.hashCode());
        result = prime * result + ((allowMultiple == null) ? 0 : allowMultiple.hashCode());
        result = prime * result + ((allowSubTypes == null) ? 0 : allowSubTypes.hashCode());
        result = prime * result + ((allowedSubTypes == null) ? 0 : allowedSubTypes.hashCode());
        result = prime * result + ((groupLabel == null) ? 0 : groupLabel.hashCode());
        result = prime * result + ((inplace == null) ? 0 : inplace.hashCode());
        result = prime * result + ((localId == null) ? 0 : localId.hashCode());
        result = prime * result + ((positional == null) ? 0 : positional.hashCode());
        result = prime * result + ((removeBorder == null) ? 0 : removeBorder.hashCode());
        result = prime * result + ((tableUI == null) ? 0 : tableUI.hashCode());
        result = prime * result + ((xmlOrder == null) ? 0 : xmlOrder.hashCode());
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
        ChildElement other = (ChildElement) obj;
        if (additionalPriorities == null) {
            if (other.additionalPriorities != null)
                return false;
        } else if (!additionalPriorities.equals(other.additionalPriorities))
            return false;
        if (allowMultiple == null) {
            if (other.allowMultiple != null)
                return false;
        } else if (!allowMultiple.equals(other.allowMultiple))
            return false;
        if (allowSubTypes == null) {
            if (other.allowSubTypes != null)
                return false;
        } else if (!allowSubTypes.equals(other.allowSubTypes))
            return false;
        if (allowedSubTypes == null) {
            if (other.allowedSubTypes != null)
                return false;
        } else if (!allowedSubTypes.equals(other.allowedSubTypes))
            return false;
        if (groupLabel == null) {
            if (other.groupLabel != null)
                return false;
        } else if (!groupLabel.equals(other.groupLabel))
            return false;
        if (inplace == null) {
            if (other.inplace != null)
                return false;
        } else if (!inplace.equals(other.inplace))
            return false;
        if (localId == null) {
            if (other.localId != null)
                return false;
        } else if (!localId.equals(other.localId))
            return false;
        if (positional == null) {
            if (other.positional != null)
                return false;
        } else if (!positional.equals(other.positional))
            return false;
        if (removeBorder == null) {
            if (other.removeBorder != null)
                return false;
        } else if (!removeBorder.equals(other.removeBorder))
            return false;
        if (tableUI == null) {
            if (other.tableUI != null)
                return false;
        } else if (!tableUI.equals(other.tableUI))
            return false;
        if (xmlOrder == null) {
            if (other.xmlOrder != null)
                return false;
        } else if (!xmlOrder.equals(other.xmlOrder))
            return false;
        return true;
    }
}
