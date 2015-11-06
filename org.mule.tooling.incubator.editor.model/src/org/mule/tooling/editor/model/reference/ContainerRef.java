package org.mule.tooling.editor.model.reference;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement
public class ContainerRef extends AbstractRef {

    private String acceptedOptionsTypes;
    
    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }
    
    @XmlAttribute
    public String getAcceptedOptionsTypes() {
        return acceptedOptionsTypes;
    }

    public void setAcceptedOptionsTypes(String acceptedOptionsTypes) {
        this.acceptedOptionsTypes = acceptedOptionsTypes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((acceptedOptionsTypes == null) ? 0 : acceptedOptionsTypes.hashCode());
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
        ContainerRef other = (ContainerRef) obj;
        if (acceptedOptionsTypes == null) {
            if (other.acceptedOptionsTypes != null)
                return false;
        } else if (!acceptedOptionsTypes.equals(other.acceptedOptionsTypes))
            return false;
        return true;
    }
}
