package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "path")
public class PathEditor extends BaseStringEditor {

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }
}
