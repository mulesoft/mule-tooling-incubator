package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum ConnectivityTesting {
    @XmlEnumValue("disabled")
    DISABLED,
    @XmlEnumValue("on")
    ON,
    @XmlEnumValue("off")
    OFF
}
