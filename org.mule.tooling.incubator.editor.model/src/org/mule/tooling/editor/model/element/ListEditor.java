package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "list")
public class ListEditor extends BaseFieldEditorElement {

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }
}
