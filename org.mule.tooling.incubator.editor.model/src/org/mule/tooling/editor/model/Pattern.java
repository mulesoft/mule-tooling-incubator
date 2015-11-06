package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Pattern extends AbstractPaletteComponent {

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }
}
