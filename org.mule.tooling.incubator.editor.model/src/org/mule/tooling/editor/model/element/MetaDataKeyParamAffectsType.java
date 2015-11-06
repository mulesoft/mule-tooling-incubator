package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum MetaDataKeyParamAffectsType {
    INPUT, OUTPUT, BOTH, AUTO
}