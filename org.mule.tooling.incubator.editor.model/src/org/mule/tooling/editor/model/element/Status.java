package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum Status {
    @XmlEnumValue("enabled")
    ENABLED,
    @XmlEnumValue("disabled")
    DISABLED;
}
