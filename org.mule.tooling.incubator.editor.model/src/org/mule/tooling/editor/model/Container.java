package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Container extends AbstractPaletteComponent {

    private Boolean allowsAllExceptInbounds;
    private String location;
    private String acceptedByElements;
    private String acceptsElements;
    private Boolean inbound;
    private String elementMatcher;
    private String defaultNestedContainer;
    private Boolean allowMulipleChildren;
    private String containerBehavior;
    private Boolean visibleInPalette;
    private String displayNameAttribute;
    private String titleColor;
    private String returnType;
    private String pathExpression;
    private Boolean forcesResponse;

    @XmlAttribute
    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    @XmlAttribute
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @XmlAttribute
    public String getAcceptedByElements() {
        return acceptedByElements;
    }

    public void setAcceptedByElements(String acceptedByElements) {
        this.acceptedByElements = acceptedByElements;
    }

    @XmlAttribute
    public String getAcceptsElements() {
        return acceptsElements;
    }

    public void setAcceptsElements(String acceptsElements) {
        this.acceptsElements = acceptsElements;
    }

    @XmlAttribute
    public Boolean getInbound() {
        return inbound;
    }

    public void setInbound(Boolean inbound) {
        this.inbound = inbound;
    }

    @XmlAttribute
    public String getElementMatcher() {
        return elementMatcher;
    }

    public void setElementMatcher(String elementMatcher) {
        this.elementMatcher = elementMatcher;
    }

    @XmlAttribute
    public String getDefaultNestedContainer() {
        return defaultNestedContainer;
    }

    public void setDefaultNestedContainer(String defaultNestedContainer) {
        this.defaultNestedContainer = defaultNestedContainer;
    }

    @XmlAttribute
    public Boolean getAllowMulipleChildren() {
        return allowMulipleChildren;
    }

    public void setAllowMulipleChildren(Boolean allowMulipleChildren) {
        this.allowMulipleChildren = allowMulipleChildren;
    }

    @XmlAttribute
    public String getContainerBehavior() {
        return containerBehavior;
    }

    public void setContainerBehavior(String containerBehavior) {
        this.containerBehavior = containerBehavior;
    }

    @XmlAttribute
    public Boolean getVisibleInPalette() {
        return visibleInPalette;
    }

    public void setVisibleInPalette(Boolean visibleInPalette) {
        this.visibleInPalette = visibleInPalette;
    }

    @XmlAttribute
    public String getDisplayNameAttribute() {
        return displayNameAttribute;
    }

    public void setDisplayNameAttribute(String displayNameAttribute) {
        this.displayNameAttribute = displayNameAttribute;
    }

    @XmlAttribute
    public String getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    @XmlAttribute
    public Boolean getAllowsAllExceptInbounds() {
        return allowsAllExceptInbounds;
    }

    public void setAllowsAllExceptInbounds(Boolean allowsAllExceptInbounds) {
        this.allowsAllExceptInbounds = allowsAllExceptInbounds;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getPathExpression() {
        return pathExpression;
    }

    public void setPathExpression(String pathExpression) {
        this.pathExpression = pathExpression;
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
        result = prime * result + ((acceptedByElements == null) ? 0 : acceptedByElements.hashCode());
        result = prime * result + ((acceptsElements == null) ? 0 : acceptsElements.hashCode());
        result = prime * result + ((allowMulipleChildren == null) ? 0 : allowMulipleChildren.hashCode());
        result = prime * result + ((allowsAllExceptInbounds == null) ? 0 : allowsAllExceptInbounds.hashCode());
        result = prime * result + ((containerBehavior == null) ? 0 : containerBehavior.hashCode());
        result = prime * result + ((defaultNestedContainer == null) ? 0 : defaultNestedContainer.hashCode());
        result = prime * result + ((displayNameAttribute == null) ? 0 : displayNameAttribute.hashCode());
        result = prime * result + ((elementMatcher == null) ? 0 : elementMatcher.hashCode());
        result = prime * result + ((forcesResponse == null) ? 0 : forcesResponse.hashCode());
        result = prime * result + ((inbound == null) ? 0 : inbound.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((pathExpression == null) ? 0 : pathExpression.hashCode());
        result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
        result = prime * result + ((titleColor == null) ? 0 : titleColor.hashCode());
        result = prime * result + ((visibleInPalette == null) ? 0 : visibleInPalette.hashCode());
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
        Container other = (Container) obj;
        if (acceptedByElements == null) {
            if (other.acceptedByElements != null)
                return false;
        } else if (!acceptedByElements.equals(other.acceptedByElements))
            return false;
        if (acceptsElements == null) {
            if (other.acceptsElements != null)
                return false;
        } else if (!acceptsElements.equals(other.acceptsElements))
            return false;
        if (allowMulipleChildren == null) {
            if (other.allowMulipleChildren != null)
                return false;
        } else if (!allowMulipleChildren.equals(other.allowMulipleChildren))
            return false;
        if (allowsAllExceptInbounds == null) {
            if (other.allowsAllExceptInbounds != null)
                return false;
        } else if (!allowsAllExceptInbounds.equals(other.allowsAllExceptInbounds))
            return false;
        if (containerBehavior == null) {
            if (other.containerBehavior != null)
                return false;
        } else if (!containerBehavior.equals(other.containerBehavior))
            return false;
        if (defaultNestedContainer == null) {
            if (other.defaultNestedContainer != null)
                return false;
        } else if (!defaultNestedContainer.equals(other.defaultNestedContainer))
            return false;
        if (displayNameAttribute == null) {
            if (other.displayNameAttribute != null)
                return false;
        } else if (!displayNameAttribute.equals(other.displayNameAttribute))
            return false;
        if (elementMatcher == null) {
            if (other.elementMatcher != null)
                return false;
        } else if (!elementMatcher.equals(other.elementMatcher))
            return false;
        if (forcesResponse == null) {
            if (other.forcesResponse != null)
                return false;
        } else if (!forcesResponse.equals(other.forcesResponse))
            return false;
        if (inbound == null) {
            if (other.inbound != null)
                return false;
        } else if (!inbound.equals(other.inbound))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (pathExpression == null) {
            if (other.pathExpression != null)
                return false;
        } else if (!pathExpression.equals(other.pathExpression))
            return false;
        if (returnType == null) {
            if (other.returnType != null)
                return false;
        } else if (!returnType.equals(other.returnType))
            return false;
        if (titleColor == null) {
            if (other.titleColor != null)
                return false;
        } else if (!titleColor.equals(other.titleColor))
            return false;
        if (visibleInPalette == null) {
            if (other.visibleInPalette != null)
                return false;
        } else if (!visibleInPalette.equals(other.visibleInPalette))
            return false;
        return true;
    }
}
