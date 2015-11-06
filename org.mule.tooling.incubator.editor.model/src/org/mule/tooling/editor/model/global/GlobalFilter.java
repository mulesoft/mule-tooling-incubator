package org.mule.tooling.editor.model.global;

import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "global-filter")
public class GlobalFilter extends AbstractGlobalElement {

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }
}
