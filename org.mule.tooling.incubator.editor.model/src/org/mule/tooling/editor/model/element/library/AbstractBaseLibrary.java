package org.mule.tooling.editor.model.element.library;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({ Jar.class, NativeLibrary.class })
@XmlRootElement
public abstract class AbstractBaseLibrary extends AbstractLibrary {

    private boolean externalPluginDependency;
    private String name;

    public AbstractBaseLibrary() {
        super();
    }

    @XmlAttribute
    public boolean isExternalPluginDependency() {
        return externalPluginDependency;
    }

    public void setExternalPluginDependency(boolean externalPluginDependency) {
        this.externalPluginDependency = externalPluginDependency;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (externalPluginDependency ? 1231 : 1237);
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
        AbstractBaseLibrary other = (AbstractBaseLibrary) obj;
        if (externalPluginDependency != other.externalPluginDependency)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}