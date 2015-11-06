package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.reference.AbstractRef;

@XmlRootElement
public class LocalRef extends AbstractRef {

    private Boolean allowConnector;
    private String refKey;

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public Boolean getAllowConnector() {
        return allowConnector;
    }

    public void setAllowConnector(Boolean allowConnector) {
        this.allowConnector = allowConnector;
    }

    @XmlAttribute
    public String getRefKey() {
        return refKey;
    }

    public void setRefKey(String refKey) {
        this.refKey = refKey;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((allowConnector == null) ? 0 : allowConnector.hashCode());
        result = prime * result + ((refKey == null) ? 0 : refKey.hashCode());
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
        LocalRef other = (LocalRef) obj;
        if (allowConnector == null) {
            if (other.allowConnector != null)
                return false;
        } else if (!allowConnector.equals(other.allowConnector))
            return false;
        if (refKey == null) {
            if (other.refKey != null)
                return false;
        } else if (!refKey.equals(other.refKey))
            return false;
        return true;
    }
}
