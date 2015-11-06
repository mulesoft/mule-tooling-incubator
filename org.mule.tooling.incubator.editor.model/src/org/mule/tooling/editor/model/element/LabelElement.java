package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "label")
public class LabelElement extends BaseFieldEditorElement {

    @Override
    public String toString() {
        return "LabelElement [getName()=" + getName() + ", getCaption()=" + getCaption() + "]";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }
}
