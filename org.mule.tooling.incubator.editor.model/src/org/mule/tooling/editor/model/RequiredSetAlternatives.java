package org.mule.tooling.editor.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.annotation.ClassPicker;

@XmlRootElement(name = "required-set-alternatives")
public class RequiredSetAlternatives extends AbstractEditorElement {

    private Boolean requiredForDataSense;
    private Boolean exclusive;
    private String message;
    @ClassPicker(mustImplement = org.mule.tooling.ui.modules.core.widgets.meta.IMessageProvider.class)
    private String messageProvider;

    @XmlAttribute
    public Boolean getRequiredForDataSense() {
        return requiredForDataSense;
    }

    public void setRequiredForDataSense(Boolean requiredForDataSense) {
        this.requiredForDataSense = requiredForDataSense;
    }

    @XmlAttribute
    public Boolean getExclusive() {
        return exclusive;
    }

    public void setExclusive(Boolean exclusive) {
        this.exclusive = exclusive;
    }

    @XmlAttribute
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @XmlAttribute
    public String getMessageProvider() {
        return messageProvider;
    }

    public void setMessageProvider(String messageProvider) {
        this.messageProvider = messageProvider;
    }

    private List<Alternative> alternatives;

    @XmlElement(name = "alternative")
    public List<Alternative> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((alternatives == null) ? 0 : alternatives.hashCode());
        result = prime * result + ((exclusive == null) ? 0 : exclusive.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((messageProvider == null) ? 0 : messageProvider.hashCode());
        result = prime * result + ((requiredForDataSense == null) ? 0 : requiredForDataSense.hashCode());
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
        RequiredSetAlternatives other = (RequiredSetAlternatives) obj;
        if (alternatives == null) {
            if (other.alternatives != null)
                return false;
        } else if (!alternatives.equals(other.alternatives))
            return false;
        if (exclusive == null) {
            if (other.exclusive != null)
                return false;
        } else if (!exclusive.equals(other.exclusive))
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (messageProvider == null) {
            if (other.messageProvider != null)
                return false;
        } else if (!messageProvider.equals(other.messageProvider))
            return false;
        if (requiredForDataSense == null) {
            if (other.requiredForDataSense != null)
                return false;
        } else if (!requiredForDataSense.equals(other.requiredForDataSense))
            return false;
        return true;
    }
}
