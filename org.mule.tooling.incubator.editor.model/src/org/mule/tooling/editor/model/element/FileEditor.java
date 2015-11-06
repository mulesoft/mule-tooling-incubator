package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "file")
public class FileEditor extends BaseStringEditor {

    private Boolean relativeToProject;
    
    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "FileEditor [getName()=" + getName() + ", getCaption()=" + getCaption() + "]";
    }

    @XmlAttribute
    public Boolean getRelativeToProject() {
        return relativeToProject;
    }

    public void setRelativeToProject(Boolean relativeToProject) {
        this.relativeToProject = relativeToProject;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((relativeToProject == null) ? 0 : relativeToProject.hashCode());
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
        FileEditor other = (FileEditor) obj;
        if (relativeToProject == null) {
            if (other.relativeToProject != null)
                return false;
        } else if (!relativeToProject.equals(other.relativeToProject))
            return false;
        return true;
    }
    
}
