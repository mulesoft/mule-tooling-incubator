package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({ NestedContainer.class, GraphicalContainer.class })
public abstract class AbstractContainer extends EditorElement {

    private String acceptsElements;
    private Boolean showsResponse;
    private Boolean collapsable;
    private Integer xmlOrder;
    private Integer rowNumber;

    @XmlAttribute
    public Boolean getShowsResponse() {
        return showsResponse;
    }

    public void setShowsResponse(Boolean showsResponse) {
        this.showsResponse = showsResponse;
    }

    @XmlAttribute
    public Boolean getCollapsable() {
        return collapsable;
    }

    public void setCollapsable(Boolean collapsable) {
        this.collapsable = collapsable;
    }

    @XmlAttribute
    public Integer getXmlOrder() {
        return xmlOrder;
    }

    public void setXmlOrder(Integer xmlOrder) {
        this.xmlOrder = xmlOrder;
    }

    @XmlAttribute
    public String getAcceptsElements() {
        return acceptsElements;
    }

    public void setAcceptsElements(String acceptsElements) {
        this.acceptsElements = acceptsElements;
    }

    @XmlAttribute
    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((acceptsElements == null) ? 0 : acceptsElements.hashCode());
        result = prime * result + ((collapsable == null) ? 0 : collapsable.hashCode());
        result = prime * result + ((rowNumber == null) ? 0 : rowNumber.hashCode());
        result = prime * result + ((showsResponse == null) ? 0 : showsResponse.hashCode());
        result = prime * result + ((xmlOrder == null) ? 0 : xmlOrder.hashCode());
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
        AbstractContainer other = (AbstractContainer) obj;
        if (acceptsElements == null) {
            if (other.acceptsElements != null)
                return false;
        } else if (!acceptsElements.equals(other.acceptsElements))
            return false;
        if (collapsable == null) {
            if (other.collapsable != null)
                return false;
        } else if (!collapsable.equals(other.collapsable))
            return false;
        if (rowNumber == null) {
            if (other.rowNumber != null)
                return false;
        } else if (!rowNumber.equals(other.rowNumber))
            return false;
        if (showsResponse == null) {
            if (other.showsResponse != null)
                return false;
        } else if (!showsResponse.equals(other.showsResponse))
            return false;
        if (xmlOrder == null) {
            if (other.xmlOrder != null)
                return false;
        } else if (!xmlOrder.equals(other.xmlOrder))
            return false;
        return true;
    }

}