package org.mule.tooling.editor.model.element.library;

import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "nativeLib")
public class NativeLibrary extends AbstractBaseLibrary {

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "NativeLibrary [getName()=" + getName() + "]";
    }
    
}
