package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum Align {
    @XmlEnumValue("left")
    LEFT,
    @XmlEnumValue("right")
    RIGTH,
}
