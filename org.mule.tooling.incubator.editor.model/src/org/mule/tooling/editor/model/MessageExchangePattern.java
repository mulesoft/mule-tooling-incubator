package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum MessageExchangePattern {
    @XmlEnumValue("OneWay")
    ONE_WAY, 
    @XmlEnumValue("RequestResponse")
    REQUEST_RESPONSE
}
