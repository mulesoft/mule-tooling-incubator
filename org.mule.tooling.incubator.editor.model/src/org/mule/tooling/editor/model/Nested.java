package org.mule.tooling.editor.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.element.BaseChildEditorElement;

@XmlRootElement
public class Nested extends EditorElement {

    private String additionalNamespaces;
    private Integer windowHeight;
    private Boolean allowAny;
    private String childPrefix;
    private String childURI;
    private Boolean required;// TODO Debe ser un editor element?

    private List<BaseChildEditorElement> childElements;

    @XmlAttribute
    public String getAdditionalNamespaces() {
        return additionalNamespaces;
    }

    public void setAdditionalNamespaces(String additionalNamespaces) {
        this.additionalNamespaces = additionalNamespaces;
    }

    @XmlAttribute
    public Integer getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(Integer windowHeight) {
        this.windowHeight = windowHeight;
    }

    @XmlAttribute
    public Boolean getAllowAny() {
        return allowAny;
    }

    public void setAllowAny(Boolean allowAny) {
        this.allowAny = allowAny;
    }

    @XmlAttribute
    public String getChildPrefix() {
        return childPrefix;
    }

    public void setChildPrefix(String childPrefix) {
        this.childPrefix = childPrefix;
    }

    @XmlAttribute
    public String getChildURI() {
        return childURI;
    }

    public void setChildURI(String childURI) {
        this.childURI = childURI;
    }

    @XmlElementRef
    public List<BaseChildEditorElement> getChildElements() {
        if (childElements == null) {
            childElements = new ArrayList<BaseChildEditorElement>();
        }
        return childElements;
    }

    public void setChildElements(List<BaseChildEditorElement> childElements) {
        this.childElements = childElements;
    }

    @Override
    public String toString() {
        return "Nested [additionalNamespaces=" + additionalNamespaces + ", childPrefix=" + childPrefix + ", childURI=" + childURI + ", getLocalId()=" + getLocalId()
                + ", getDescription()=" + getDescription() + ", getCaption()=" + getCaption() + "]";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((additionalNamespaces == null) ? 0 : additionalNamespaces.hashCode());
        result = prime * result + ((allowAny == null) ? 0 : allowAny.hashCode());
        result = prime * result + ((childElements == null) ? 0 : childElements.hashCode());
        result = prime * result + ((childPrefix == null) ? 0 : childPrefix.hashCode());
        result = prime * result + ((childURI == null) ? 0 : childURI.hashCode());
        result = prime * result + ((required == null) ? 0 : required.hashCode());
        result = prime * result + ((windowHeight == null) ? 0 : windowHeight.hashCode());
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
        Nested other = (Nested) obj;
        if (additionalNamespaces == null) {
            if (other.additionalNamespaces != null)
                return false;
        } else if (!additionalNamespaces.equals(other.additionalNamespaces))
            return false;
        if (allowAny == null) {
            if (other.allowAny != null)
                return false;
        } else if (!allowAny.equals(other.allowAny))
            return false;
        if (childElements == null) {
            if (other.childElements != null)
                return false;
        } else if (!childElements.equals(other.childElements))
            return false;
        if (childPrefix == null) {
            if (other.childPrefix != null)
                return false;
        } else if (!childPrefix.equals(other.childPrefix))
            return false;
        if (childURI == null) {
            if (other.childURI != null)
                return false;
        } else if (!childURI.equals(other.childURI))
            return false;
        if (required == null) {
            if (other.required != null)
                return false;
        } else if (!required.equals(other.required))
            return false;
        if (windowHeight == null) {
            if (other.windowHeight != null)
                return false;
        } else if (!windowHeight.equals(other.windowHeight))
            return false;
        return true;
    }
}
