package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.element.Category;

@XmlRootElement(name = "multi-source")
public class MultiSource extends EditorElement {

    private Boolean allowsInbound;
    private Boolean propertiesEditable;
    private Boolean allowsOutbound;
    private Boolean allowsMessageProcessors;
    private String scopeLabel;
    private Boolean allowsMultipleChildren;
    private Category categoryId;//TODO xq no simplement category?
    private String id;// TODO se usa para algo??
    private Boolean forcesResponse;
    
    @XmlAttribute
    public Boolean getAllowsInbound() {
        return allowsInbound;
    }

    public void setAllowsInbound(Boolean allowsInbound) {
        this.allowsInbound = allowsInbound;
    }

    @XmlAttribute
    public Boolean getPropertiesEditable() {
        return propertiesEditable;
    }

    public void setPropertiesEditable(Boolean propertiesEditable) {
        this.propertiesEditable = propertiesEditable;
    }

    @XmlAttribute
    public Boolean getAllowsOutbound() {
        return allowsOutbound;
    }

    public void setAllowsOutbound(Boolean allowsOutbound) {
        this.allowsOutbound = allowsOutbound;
    }

    @XmlAttribute
    public Boolean getAllowsMessageProcessors() {
        return allowsMessageProcessors;
    }

    public void setAllowsMessageProcessors(Boolean allowsMessageProcessors) {
        this.allowsMessageProcessors = allowsMessageProcessors;
    }

    @XmlAttribute
    public String getScopeLabel() {
        return scopeLabel;
    }

    public void setScopeLabel(String scopeLabel) {
        this.scopeLabel = scopeLabel;
    }

    @XmlAttribute
    public Boolean getAllowsMultipleChildren() {
        return allowsMultipleChildren;
    }

    public void setAllowsMultipleChildren(Boolean allowsMultipleChildren) {
        this.allowsMultipleChildren = allowsMultipleChildren;
    }

    @XmlAttribute
    public Category getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Category categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute
    public Boolean getForcesResponse() {
        return forcesResponse;
    }

    public void setForcesResponse(Boolean forcesResponse) {
        this.forcesResponse = forcesResponse;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((allowsInbound == null) ? 0 : allowsInbound.hashCode());
        result = prime * result + ((allowsMessageProcessors == null) ? 0 : allowsMessageProcessors.hashCode());
        result = prime * result + ((allowsMultipleChildren == null) ? 0 : allowsMultipleChildren.hashCode());
        result = prime * result + ((allowsOutbound == null) ? 0 : allowsOutbound.hashCode());
        result = prime * result + ((categoryId == null) ? 0 : categoryId.hashCode());
        result = prime * result + ((forcesResponse == null) ? 0 : forcesResponse.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((propertiesEditable == null) ? 0 : propertiesEditable.hashCode());
        result = prime * result + ((scopeLabel == null) ? 0 : scopeLabel.hashCode());
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
        MultiSource other = (MultiSource) obj;
        if (allowsInbound == null) {
            if (other.allowsInbound != null)
                return false;
        } else if (!allowsInbound.equals(other.allowsInbound))
            return false;
        if (allowsMessageProcessors == null) {
            if (other.allowsMessageProcessors != null)
                return false;
        } else if (!allowsMessageProcessors.equals(other.allowsMessageProcessors))
            return false;
        if (allowsMultipleChildren == null) {
            if (other.allowsMultipleChildren != null)
                return false;
        } else if (!allowsMultipleChildren.equals(other.allowsMultipleChildren))
            return false;
        if (allowsOutbound == null) {
            if (other.allowsOutbound != null)
                return false;
        } else if (!allowsOutbound.equals(other.allowsOutbound))
            return false;
        if (categoryId != other.categoryId)
            return false;
        if (forcesResponse == null) {
            if (other.forcesResponse != null)
                return false;
        } else if (!forcesResponse.equals(other.forcesResponse))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (propertiesEditable == null) {
            if (other.propertiesEditable != null)
                return false;
        } else if (!propertiesEditable.equals(other.propertiesEditable))
            return false;
        if (scopeLabel == null) {
            if (other.scopeLabel != null)
                return false;
        } else if (!scopeLabel.equals(other.scopeLabel))
            return false;
        return true;
    }
}
