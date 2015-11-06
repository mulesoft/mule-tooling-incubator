package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "url")
public class UrlEditor extends BaseStringEditor {

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }
}
