package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "nested-container")
public class NestedContainer extends AbstractContainer {

    @Override
    public String toString() {
        return "NestedContainer [getLocalId()=" + getLocalId() + "]";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }
}
