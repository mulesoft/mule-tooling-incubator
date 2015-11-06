package org.mule.tooling.editor.model.element;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement
public class SwitchCase extends BaseChildEditorElement {

    private String name;
    private String controlled;
    private List<Case> cases;

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getControlled() {
        return controlled;
    }

    public void setControlled(String controlled) {
        this.controlled = controlled;
    }

    @XmlElementRef
    public List<Case> getCases() {
        return cases;
    }

    public void setCases(List<Case> cases) {
        this.cases = cases;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((cases == null) ? 0 : cases.hashCode());
        result = prime * result + ((controlled == null) ? 0 : controlled.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        SwitchCase other = (SwitchCase) obj;
        if (cases == null) {
            if (other.cases != null)
                return false;
        } else if (!cases.equals(other.cases))
            return false;
        if (controlled == null) {
            if (other.controlled != null)
                return false;
        } else if (!controlled.equals(other.controlled))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
