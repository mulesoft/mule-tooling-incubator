package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Keyword extends AbstractEditorElement {

    private Boolean isTopHit;
    private String value;
    private Integer weight;

    @XmlAttribute
    public Boolean getIsTopHit() {
        return isTopHit;
    }

    public void setIsTopHit(Boolean isTopHit) {
        this.isTopHit = isTopHit;
    }

    @XmlAttribute
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @XmlAttribute
    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Keyword [isTopHit=" + isTopHit + ", value=" + value + ", weight=" + weight + "]";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((isTopHit == null) ? 0 : isTopHit.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((weight == null) ? 0 : weight.hashCode());
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
        Keyword other = (Keyword) obj;
        if (isTopHit == null) {
            if (other.isTopHit != null)
                return false;
        } else if (!isTopHit.equals(other.isTopHit))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        if (weight == null) {
            if (other.weight != null)
                return false;
        } else if (!weight.equals(other.weight))
            return false;
        return true;
    }

}
