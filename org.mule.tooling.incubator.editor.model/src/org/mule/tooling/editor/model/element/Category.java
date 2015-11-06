package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum Category {
    @XmlEnumValue("org.mule.tooling.category.scopes")
    SCOPES,
    @XmlEnumValue("org.mule.tooling.category.cloudconnectors")
    CLOUD_CONNECTORS,
    @XmlEnumValue("org.mule.tooling.category.flowControl")
    FLOW_CONTROL,
    @XmlEnumValue("org.mule.tooling.category.endpoints")
    ENDPOINTS,
    @XmlEnumValue("org.mule.tooling.category.core")
    CORE,
    @XmlEnumValue("category:org.mule.tooling.ui.modules.core.exceptions")
    EXCEPTIONS
}
