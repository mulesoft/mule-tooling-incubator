package org.mule.tooling.editor.model.element.library;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "or")
public class LibrarySet extends AbstractLibrary {

    private List<AbstractBaseLibrary> libraries;

    @XmlElementRef
    public List<AbstractBaseLibrary> getLibraries() {
        if (libraries == null) {
            libraries = new ArrayList<AbstractBaseLibrary>();
        }
        return libraries;
    }

    public void setLibraries(List<AbstractBaseLibrary> libraries) {
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
        LibrarySet other = (LibrarySet) obj;
        if (libraries == null) {
            if (other.libraries != null)
                return false;
        } else if (!libraries.equals(other.libraries))
            return false;
        return true;
    }
}
