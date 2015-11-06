package org.mule.tooling.editor.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "namespace")
public class Namespace extends AbstractEditorElement {

    private String prefix;

    private String url;

    private List<EditorElement> components;

    @XmlAttribute
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @XmlAttribute
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlElementRef
    public List<EditorElement> getComponents() {
        if (components == null) {
            components = new ArrayList<EditorElement>();
        }
        return components;
    }

    public void setComponents(List<EditorElement> components) {
        this.components = components;
    }

    @Override
    public String toString() {
        return "Namespace [prefix=" + prefix + ", url=" + url + "]";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((components == null) ? 0 : components.hashCode());
        result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
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
        Namespace other = (Namespace) obj;
        if (components == null) {
            if (other.components != null)
                return false;
        } else if (!components.equals(other.components))
            return false;
        if (prefix == null) {
            if (other.prefix != null)
                return false;
        } else if (!prefix.equals(other.prefix))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

}
