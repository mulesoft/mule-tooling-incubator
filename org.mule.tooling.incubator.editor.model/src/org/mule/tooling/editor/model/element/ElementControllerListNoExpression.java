package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "element-controller-list-no-expression")
public class ElementControllerListNoExpression extends AbstractElementController {

    private String listName;

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((listName == null) ? 0 : listName.hashCode());
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
        ElementControllerListNoExpression other = (ElementControllerListNoExpression) obj;
        if (listName == null) {
            if (other.listName != null)
                return false;
        } else if (!listName.equals(other.listName))
            return false;
        return true;
    }
}
