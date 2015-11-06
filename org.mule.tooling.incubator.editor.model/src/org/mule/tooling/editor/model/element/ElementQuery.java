package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "element-query")
public class ElementQuery extends BaseFieldEditorElement {

    private String associatedConfig;

    private Status orderBy = Status.ENABLED;
    private Status nativeQuery = Status.ENABLED;
    private Status orOperator = Status.ENABLED;
    private Status andOperator = Status.ENABLED;
    private Status offset = Status.ENABLED;
    private Status limit = Status.ENABLED;

    @XmlAttribute
    public Status getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Status orderBy) {
        this.orderBy = orderBy;
    }

    @XmlAttribute
    public Status getNativeQuery() {
        return nativeQuery;
    }

    public void setNativeQuery(Status nativeQuery) {
        this.nativeQuery = nativeQuery;
    }

    @XmlAttribute
    public Status getOrOperator() {
        return orOperator;
    }

    public void setOrOperator(Status orOperator) {
        this.orOperator = orOperator;
    }

    @XmlAttribute
    public Status getAndOperator() {
        return andOperator;
    }

    public void setAndOperator(Status andOperator) {
        this.andOperator = andOperator;
    }

    @XmlAttribute
    public Status getOffset() {
        return offset;
    }

    public void setOffset(Status offset) {
        this.offset = offset;
    }

    @XmlAttribute
    public Status getLimit() {
        return limit;
    }

    public void setLimit(Status limit) {
        this.limit = limit;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getAssociatedConfig() {
        return associatedConfig;
    }

    public void setAssociatedConfig(String associatedConfig) {
        this.associatedConfig = associatedConfig;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((andOperator == null) ? 0 : andOperator.hashCode());
        result = prime * result + ((associatedConfig == null) ? 0 : associatedConfig.hashCode());
        result = prime * result + ((limit == null) ? 0 : limit.hashCode());
        result = prime * result + ((nativeQuery == null) ? 0 : nativeQuery.hashCode());
        result = prime * result + ((offset == null) ? 0 : offset.hashCode());
        result = prime * result + ((orOperator == null) ? 0 : orOperator.hashCode());
        result = prime * result + ((orderBy == null) ? 0 : orderBy.hashCode());
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
        ElementQuery other = (ElementQuery) obj;
        if (andOperator != other.andOperator)
            return false;
        if (associatedConfig == null) {
            if (other.associatedConfig != null)
                return false;
        } else if (!associatedConfig.equals(other.associatedConfig))
            return false;
        if (limit != other.limit)
            return false;
        if (nativeQuery != other.nativeQuery)
            return false;
        if (offset != other.offset)
            return false;
        if (orOperator != other.orOperator)
            return false;
        if (orderBy != other.orderBy)
            return false;
        return true;
    }
}
