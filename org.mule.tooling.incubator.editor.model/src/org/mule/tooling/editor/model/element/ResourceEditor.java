package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "resource")
public class ResourceEditor extends BaseFieldEditorElement {

    private String containerLevel;// TODO Solo existe un valor y es project...es un ENUM?
    private String initialPattern;
    private String resourceTypes;

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ResourceEditor [getName()=" + getName() + ", getCaption()=" + getCaption() + "]";
    }

    @XmlAttribute
    public String getContainerLevel() {
        return containerLevel;
    }

    public void setContainerLevel(String containerLevel) {
        this.containerLevel = containerLevel;
    }

    @XmlAttribute
    public String getInitialPattern() {
        return initialPattern;
    }

    public void setInitialPattern(String initialPattern) {
        this.initialPattern = initialPattern;
    }

    @XmlAttribute
    public String getResourceTypes() {
        return resourceTypes;
    }

    public void setResourceTypes(String resourceTypes) {
        this.resourceTypes = resourceTypes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((containerLevel == null) ? 0 : containerLevel.hashCode());
        result = prime * result + ((initialPattern == null) ? 0 : initialPattern.hashCode());
        result = prime * result + ((resourceTypes == null) ? 0 : resourceTypes.hashCode());
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
        ResourceEditor other = (ResourceEditor) obj;
        if (containerLevel == null) {
            if (other.containerLevel != null)
                return false;
        } else if (!containerLevel.equals(other.containerLevel))
            return false;
        if (initialPattern == null) {
            if (other.initialPattern != null)
                return false;
        } else if (!initialPattern.equals(other.initialPattern))
            return false;
        if (resourceTypes == null) {
            if (other.resourceTypes != null)
                return false;
        } else if (!resourceTypes.equals(other.resourceTypes))
            return false;
        return true;
    }

}
