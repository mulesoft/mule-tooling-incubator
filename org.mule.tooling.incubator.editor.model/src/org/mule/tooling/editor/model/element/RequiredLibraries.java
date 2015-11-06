package org.mule.tooling.editor.model.element;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.AbstractEditorElement;
import org.mule.tooling.editor.model.IElementVisitor;
import org.mule.tooling.editor.model.element.library.AbstractLibrary;

@XmlRootElement(name = "require")
public class RequiredLibraries extends AbstractEditorElement {

    @Override
    public String toString() {
        return "RequiredLibraries";
    }

    private List<AbstractLibrary> libraries;

    @XmlElementRef
    public List<AbstractLibrary> getLibraries() {
        if (libraries == null) {
            libraries = new ArrayList<AbstractLibrary>();
        }
        return libraries;
    }

    public void setLibraries(List<AbstractLibrary> libraries) {
        this.libraries = libraries;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((libraries == null) ? 0 : libraries.hashCode());
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
        RequiredLibraries other = (RequiredLibraries) obj;
        if (libraries == null) {
            if (other.libraries != null)
                return false;
        } else if (!libraries.equals(other.libraries))
            return false;
        return true;
    }
}
