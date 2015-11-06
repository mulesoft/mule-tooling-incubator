package org.mule.tooling.editor.model.global;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "cloud-connector-message-source")
public class CloudConnectorMessageSource extends AbstractGlobalElement {

    private String inboundLocalName;
    private Boolean supportsOutbound;

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getInboundLocalName() {
        return inboundLocalName;
    }

    public void setInboundLocalName(String inboundLocalName) {
        this.inboundLocalName = inboundLocalName;
    }
    
    @XmlAttribute
    public Boolean getSupportsOutbound() {
        return supportsOutbound;
    }

    public void setSupportsOutbound(Boolean supportsOutbound) {
        this.supportsOutbound = supportsOutbound;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((inboundLocalName == null) ? 0 : inboundLocalName.hashCode());
        result = prime * result + ((supportsOutbound == null) ? 0 : supportsOutbound.hashCode());
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
        CloudConnectorMessageSource other = (CloudConnectorMessageSource) obj;
        if (inboundLocalName == null) {
            if (other.inboundLocalName != null)
                return false;
        } else if (!inboundLocalName.equals(other.inboundLocalName))
            return false;
        if (supportsOutbound == null) {
            if (other.supportsOutbound != null)
                return false;
        } else if (!supportsOutbound.equals(other.supportsOutbound))
            return false;
        return true;
    }
}
