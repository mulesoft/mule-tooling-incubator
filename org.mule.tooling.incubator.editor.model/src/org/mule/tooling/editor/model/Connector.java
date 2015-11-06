package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Connector extends AbstractPaletteComponent {

    private ConnectivityTesting connectivityTesting;

    @XmlAttribute
    public ConnectivityTesting getConnectivityTesting() {
        return connectivityTesting;
    }

    public void setConnectivityTesting(ConnectivityTesting connectivityTesting) {
        this.connectivityTesting = connectivityTesting;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((connectivityTesting == null) ? 0 : connectivityTesting.hashCode());
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
        Connector other = (Connector) obj;
        if (connectivityTesting != other.connectivityTesting)
            return false;
        return true;
    }
}
