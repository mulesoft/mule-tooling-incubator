package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Component extends EditorElement {

    private Boolean processesResponse;

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public Boolean getProcessesResponse() {
        return processesResponse;
    }

    public void setProcessesResponse(Boolean processesResponse) {
        this.processesResponse = processesResponse;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((processesResponse == null) ? 0 : processesResponse.hashCode());
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
        Component other = (Component) obj;
        if (processesResponse == null) {
            if (other.processesResponse != null)
                return false;
        } else if (!processesResponse.equals(other.processesResponse))
            return false;
        return true;
    }
}
