package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "string-map")
public class StringMap extends AbstractElementController {

    // TODO: EEE Review this names and put something more useful. This probably are the root field reference name, and the child field reference name
    private String ref;
    private String ref1;
    private String listName;

    @XmlAttribute(name = "ref")
    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    @XmlAttribute(name = "ref1")
    public String getRef1() {
        return ref1;
    }

    public void setRef1(String ref1) {
        this.ref1 = ref1;
    }

    @XmlAttribute
    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((listName == null) ? 0 : listName.hashCode());
        result = prime * result + ((ref == null) ? 0 : ref.hashCode());
        result = prime * result + ((ref1 == null) ? 0 : ref1.hashCode());
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
        StringMap other = (StringMap) obj;
        if (listName == null) {
            if (other.listName != null)
                return false;
        } else if (!listName.equals(other.listName))
            return false;
        if (ref == null) {
            if (other.ref != null)
                return false;
        } else if (!ref.equals(other.ref))
            return false;
        if (ref1 == null) {
            if (other.ref1 != null)
                return false;
        } else if (!ref1.equals(other.ref1))
            return false;
        return true;
    }
}
