package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Flow extends AbstractPaletteComponent {

    private String modelGenerator;
    private Boolean causesSplit;
    private String returnType;
    private Boolean processesResponse;

    @XmlAttribute
    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    @XmlAttribute
    public String getModelGenerator() {
        return modelGenerator;
    }

    public void setModelGenerator(String modelGenerator) {
        this.modelGenerator = modelGenerator;
    }

    @XmlAttribute
    public Boolean getCausesSplit() {
        return causesSplit;
    }

    public void setCausesSplit(Boolean causesSplit) {
        this.causesSplit = causesSplit;
    }

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
        result = prime * result + ((causesSplit == null) ? 0 : causesSplit.hashCode());
        result = prime * result + ((modelGenerator == null) ? 0 : modelGenerator.hashCode());
        result = prime * result + ((processesResponse == null) ? 0 : processesResponse.hashCode());
        result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
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
        Flow other = (Flow) obj;
        if (causesSplit == null) {
            if (other.causesSplit != null)
                return false;
        } else if (!causesSplit.equals(other.causesSplit))
            return false;
        if (modelGenerator == null) {
            if (other.modelGenerator != null)
                return false;
        } else if (!modelGenerator.equals(other.modelGenerator))
            return false;
        if (processesResponse == null) {
            if (other.processesResponse != null)
                return false;
        } else if (!processesResponse.equals(other.processesResponse))
            return false;
        if (returnType == null) {
            if (other.returnType != null)
                return false;
        } else if (!returnType.equals(other.returnType))
            return false;
        return true;
    }
}
