package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.mule.tooling.editor.model.AbstractEditorElement;

@XmlSeeAlso({ Mode.class, NoOperation.class })
public abstract class AbstractMode extends AbstractEditorElement {

}
