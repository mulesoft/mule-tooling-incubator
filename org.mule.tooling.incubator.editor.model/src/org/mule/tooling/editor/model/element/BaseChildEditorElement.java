package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.mule.tooling.editor.model.AbstractEditorElement;

@XmlSeeAlso({ AbstractElementController.class, AttributeCategory.class, BaseFieldEditorElement.class, Button.class, Group.class, Horizontal.class, SwitchCase.class })
public abstract class BaseChildEditorElement extends AbstractEditorElement {

    private String caption;
    // TODO: See if description can be removed from Button.class
    private String description;

    @XmlAttribute
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @XmlAttribute
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "BaseEditorElement [caption=" + caption + ", description=" + description + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((caption == null) ? 0 : caption.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
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
        BaseChildEditorElement other = (BaseChildEditorElement) obj;
        if (caption == null) {
            if (other.caption != null)
                return false;
        } else if (!caption.equals(other.caption))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        return true;
    }

}
