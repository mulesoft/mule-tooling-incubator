package org.mule.tooling.editor.model.element;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "attribute-category")
public class AttributeCategory extends BaseChildEditorElement {

    private String versions;
    private String id;
    private String topAnchor;

    private List<BaseChildEditorElement> childs;

    @XmlElementRef
    public List<BaseChildEditorElement> getChilds() {
        if (childs == null) {
            childs = new ArrayList<BaseChildEditorElement>();
        }
        return childs;
    }

    public void setChilds(List<BaseChildEditorElement> childs) {
        this.childs = childs;
    }

    @XmlAttribute
    public String getVersions() {
        return versions;
    }

    public void setVersions(String versions) {
        this.versions = versions;
    }

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute
    public String getTopAnchor() {
        return topAnchor;
    }

    public void setTopAnchor(String topAnchor) {
        this.topAnchor = topAnchor;
    }

    @Override
    public String toString() {
        return "AttributeCategory (" + super.toString() + ")";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((childs == null) ? 0 : childs.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((topAnchor == null) ? 0 : topAnchor.hashCode());
        result = prime * result + ((versions == null) ? 0 : versions.hashCode());
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
        AttributeCategory other = (AttributeCategory) obj;
        if (childs == null) {
            if (other.childs != null)
                return false;
        } else if (!childs.equals(other.childs))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (topAnchor == null) {
            if (other.topAnchor != null)
                return false;
        } else if (!topAnchor.equals(other.topAnchor))
            return false;
        if (versions == null) {
            if (other.versions != null)
                return false;
        } else if (!versions.equals(other.versions))
            return false;
        return true;
    }
}
