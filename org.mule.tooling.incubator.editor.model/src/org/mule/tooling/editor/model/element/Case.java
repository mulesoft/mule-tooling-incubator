package org.mule.tooling.editor.model.element;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.AbstractEditorElement;
import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement
public class Case extends AbstractEditorElement {

    private String id;
    private List<BaseChildEditorElement> childElements;

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

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((childElements == null) ? 0 : childElements.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        Case other = (Case) obj;
        if (childElements == null) {
            if (other.childElements != null)
                return false;
        } else if (!childElements.equals(other.childElements))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
