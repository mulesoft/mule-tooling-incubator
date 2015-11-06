package org.mule.tooling.editor.model.element.library;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement
public class Jar extends AbstractBaseLibrary {

    private String className;
    private String emptyLocationLabel;
    private String targetFolder;
    private String targetRuntimeFolder;
    private String fileName;

    @XmlAttribute
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @XmlAttribute
    public String getEmptyLocationLabel() {
        return emptyLocationLabel;
    }

    public void setEmptyLocationLabel(String emptyLocationLabel) {
        this.emptyLocationLabel = emptyLocationLabel;
    }

    @XmlAttribute
    public String getTargetFolder() {
        return targetFolder;
    }

    public void setTargetFolder(String targetFolder) {
        this.targetFolder = targetFolder;
    }

    @XmlAttribute
    public String getTargetRuntimeFolder() {
        return targetRuntimeFolder;
    }

    public void setTargetRuntimeFolder(String targetRuntimeFolder) {
        this.targetRuntimeFolder = targetRuntimeFolder;
    }

    @XmlAttribute
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Jar [className=" + className + ", fileName=" + fileName + ", getName()=" + getName() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((emptyLocationLabel == null) ? 0 : emptyLocationLabel.hashCode());
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + ((targetFolder == null) ? 0 : targetFolder.hashCode());
        result = prime * result + ((targetRuntimeFolder == null) ? 0 : targetRuntimeFolder.hashCode());
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
        Jar other = (Jar) obj;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        if (emptyLocationLabel == null) {
            if (other.emptyLocationLabel != null)
                return false;
        } else if (!emptyLocationLabel.equals(other.emptyLocationLabel))
            return false;
        if (fileName == null) {
            if (other.fileName != null)
                return false;
        } else if (!fileName.equals(other.fileName))
            return false;
        if (targetFolder == null) {
            if (other.targetFolder != null)
                return false;
        } else if (!targetFolder.equals(other.targetFolder))
            return false;
        if (targetRuntimeFolder == null) {
            if (other.targetRuntimeFolder != null)
                return false;
        } else if (!targetRuntimeFolder.equals(other.targetRuntimeFolder))
            return false;
        return true;
    }
    
}
