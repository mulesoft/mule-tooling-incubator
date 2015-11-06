package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "no-operation")
public class NoOperation extends AbstractMode {

    private String abstractElement;
    private String connectorName;

    @XmlAttribute
    public String getAbstractElement() {
        return abstractElement;
    }

    public void setAbstractElement(String abstractElement) {
        this.abstractElement = abstractElement;
    }

    @XmlAttribute(name = "connector-name")
    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "NoOperation [abstractElement=" + abstractElement + ", connectorName=" + connectorName + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((abstractElement == null) ? 0 : abstractElement.hashCode());
        result = prime * result + ((connectorName == null) ? 0 : connectorName.hashCode());
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
        NoOperation other = (NoOperation) obj;
        if (abstractElement == null) {
            if (other.abstractElement != null)
                return false;
        } else if (!abstractElement.equals(other.abstractElement))
            return false;
        if (connectorName == null) {
            if (other.connectorName != null)
                return false;
        } else if (!connectorName.equals(other.connectorName))
            return false;
        return true;
    }

}
