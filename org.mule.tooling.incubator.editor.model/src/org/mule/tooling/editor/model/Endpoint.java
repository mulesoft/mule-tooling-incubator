package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Endpoint extends AbstractPaletteComponent {

    private MessageExchangePattern defaultMep;
    private String inboundLocalName;
    private String outboundLocalName;
    private Boolean supportsOutbound;
    
    @Override
    public String toString() {
        return "Endpoint (" + super.toString() + ")";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public MessageExchangePattern getDefaultMep() {
        return defaultMep;
    }

    public void setDefaultMep(MessageExchangePattern defaultMep) {
        this.defaultMep = defaultMep;
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

    @XmlAttribute
    public String getOutboundLocalName() {
        return outboundLocalName;
    }

    public void setOutboundLocalName(String outboundLocalName) {
        this.outboundLocalName = outboundLocalName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((defaultMep == null) ? 0 : defaultMep.hashCode());
        result = prime * result + ((inboundLocalName == null) ? 0 : inboundLocalName.hashCode());
        result = prime * result + ((outboundLocalName == null) ? 0 : outboundLocalName.hashCode());
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
        Endpoint other = (Endpoint) obj;
        if (defaultMep != other.defaultMep)
            return false;
        if (inboundLocalName == null) {
            if (other.inboundLocalName != null)
                return false;
        } else if (!inboundLocalName.equals(other.inboundLocalName))
            return false;
        if (outboundLocalName == null) {
            if (other.outboundLocalName != null)
                return false;
        } else if (!outboundLocalName.equals(other.outboundLocalName))
            return false;
        if (supportsOutbound == null) {
            if (other.supportsOutbound != null)
                return false;
        } else if (!supportsOutbound.equals(other.supportsOutbound))
            return false;
        return true;
    }
}
