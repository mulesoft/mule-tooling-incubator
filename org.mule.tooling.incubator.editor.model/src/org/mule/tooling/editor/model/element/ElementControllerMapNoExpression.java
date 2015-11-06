package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "element-controller-map-no-expression")
public class ElementControllerMapNoExpression extends AbstractElementController {

    private String metaDataStaticKey;
    private String mapName;
    private String defaultValue;

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getMetaDataStaticKey() {
        return metaDataStaticKey;
    }

    public void setMetaDataStaticKey(String metaDataStaticKey) {
        this.metaDataStaticKey = metaDataStaticKey;
    }

    @XmlAttribute
    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    @XmlAttribute
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((mapName == null) ? 0 : mapName.hashCode());
        result = prime * result + ((metaDataStaticKey == null) ? 0 : metaDataStaticKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ElementControllerMapNoExpression other = (ElementControllerMapNoExpression) obj;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (mapName == null) {
            if (other.mapName != null)
                return false;
        } else if (!mapName.equals(other.mapName))
            return false;
        if (metaDataStaticKey == null) {
            if (other.metaDataStaticKey != null)
                return false;
        } else if (!metaDataStaticKey.equals(other.metaDataStaticKey))
            return false;
        return true;
    }
}
