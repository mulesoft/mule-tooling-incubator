package org.mule.tooling.editor.model.global;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;
import org.mule.tooling.editor.model.MessageExchangePattern;

@XmlRootElement(name = "global-endpoint")
public class GlobalEndpoint extends AbstractGlobalElement {

    private MessageExchangePattern defaultMep;
    private Boolean supportsOutbound;

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
        result = prime * result + ((defaultMep == null) ? 0 : defaultMep.hashCode());
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
        GlobalEndpoint other = (GlobalEndpoint) obj;
        if (defaultMep != other.defaultMep)
            return false;
        if (supportsOutbound == null) {
            if (other.supportsOutbound != null)
                return false;
        } else if (!supportsOutbound.equals(other.supportsOutbound))
            return false;
        return true;
    }
}
