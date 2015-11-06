package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum ModeType {
    @XmlEnumValue("outbound")
    OUTBOUND,
    @XmlEnumValue("inbound")
    INBOUND,
    @XmlEnumValue("both")
    BOTH
}
