package org.mule.tooling.editor.model;


public interface IComponentElement {
    void accept(IElementVisitor visitor);
}
