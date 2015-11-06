package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.AbstractEditorElement;
import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement
public class UseMetaData extends AbstractEditorElement {

    private String name;
    private String caption;

    @Override
    public String toString() {
        return "UseMetaData [getName()=" + getName() + ", getCaption()=" + getCaption() + "]";
    }

    // TODO: Review how this is used. Maybe it doesn't need to extend this class.
    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((caption == null) ? 0 : caption.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        UseMetaData other = (UseMetaData) obj;
        if (caption == null) {
            if (other.caption != null)
                return false;
        } else if (!caption.equals(other.caption))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
