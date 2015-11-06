package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum MetaDataBehaviour {
    @XmlEnumValue("off")
    OFF, @XmlEnumValue("static")
    STATIC, @XmlEnumValue("dynamic")
    DYNAMIC
}
