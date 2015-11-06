package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public abstract class AbstractEditorElement implements IComponentElement {

    private String versions;
    private String deprecatedVersions;
    private String deprecatedMessage; // TODO Solo se usa en enum, integer y String, pero me parece mejor que este a nivel de cualquier elemento

    @XmlAttribute
    public String getVersions() {
        return versions;
    }

    public void setVersions(String versions) {
        this.versions = versions;
    }

    @XmlAttribute
    public String getDeprecatedVersions() {
        return deprecatedVersions;
    }

    public void setDeprecatedVersions(String deprecatedVersions) {
        this.deprecatedVersions = deprecatedVersions;
    }

    @XmlAttribute
    public String getDeprecatedMessage() {
        return deprecatedMessage;
    }

    public void setDeprecatedMessage(String deprecatedMessage) {
        this.deprecatedMessage = deprecatedMessage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deprecatedMessage == null) ? 0 : deprecatedMessage.hashCode());
        result = prime * result + ((deprecatedVersions == null) ? 0 : deprecatedVersions.hashCode());
        result = prime * result + ((versions == null) ? 0 : versions.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractEditorElement other = (AbstractEditorElement) obj;
        if (deprecatedMessage == null) {
            if (other.deprecatedMessage != null)
                return false;
        } else if (!deprecatedMessage.equals(other.deprecatedMessage))
            return false;
        if (deprecatedVersions == null) {
            if (other.deprecatedVersions != null)
                return false;
        } else if (!deprecatedVersions.equals(other.deprecatedVersions))
            return false;
        if (versions == null) {
            if (other.versions != null)
                return false;
        } else if (!versions.equals(other.versions))
            return false;
        return true;
    }
}
