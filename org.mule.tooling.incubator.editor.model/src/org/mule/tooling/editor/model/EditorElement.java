package org.mule.tooling.editor.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.mule.tooling.editor.annotation.ClassPicker;
import org.mule.tooling.editor.model.element.AttributeCategory;
import org.mule.tooling.editor.model.global.AbstractGlobalElement;

@XmlSeeAlso({ AbstractContainer.class, AbstractGlobalElement.class, AbstractPaletteComponent.class, Component.class, Nested.class, MultiSource.class})
public abstract class EditorElement extends AbstractEditorElement {

    private String caption;
    private String description;
    private String localId;
    private String xmlname;
    private String icon;
    private String image;
    private Boolean isAbstract;
    @ClassPicker(mustImplement = org.mule.tooling.ui.modules.core.widgets.meta.IComponentValidator.class)
    private String componentValidator;
    private List<AttributeCategory> attributeCategories;
    private Boolean doNotInherit;
    private String defaultDocName;// Solo lo usan Container y Component
    private Boolean showProposalInXML;// Solo se usa en endpoint,connector,global-endpoint
    private String extendsElement;
    private Boolean supportsInbound;
    private String aliasId;
    private Boolean hiddenFromXML;

    @XmlAttribute(required = true)
    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    @XmlAttribute
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlAttribute
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @XmlAttribute
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @XmlAttribute
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @XmlElement(name = "attribute-category")
    public List<AttributeCategory> getAttributeCategories() {
        if (attributeCategories == null) {
            attributeCategories = new ArrayList<AttributeCategory>();
        }
        return attributeCategories;
    }

    public void setAttributeCategories(List<AttributeCategory> attributeCategories) {
        this.attributeCategories = attributeCategories;
    }

    @XmlAttribute
    public String getXmlname() {
        return xmlname;
    }

    public void setXmlname(String xmlname) {
        this.xmlname = xmlname;
    }

    @Override
    public String toString() {
        return " [caption=" + caption + ", localId=" + localId + "]";
    }

    @XmlAttribute(name = "abstract")
    public Boolean getIsAbstract() {
        return isAbstract;
    }

    public void setIsAbstract(Boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    @XmlAttribute
    public String getComponentValidator() {
        return componentValidator;
    }

    public void setComponentValidator(String componentValidator) {
        this.componentValidator = componentValidator;
    }

    @XmlAttribute
    public Boolean getDoNotInherit() {
        return doNotInherit;
    }

    public void setDoNotInherit(Boolean doNotInherit) {
        this.doNotInherit = doNotInherit;
    }

    @XmlAttribute
    public String getDefaultDocName() {
        return defaultDocName;
    }

    public void setDefaultDocName(String defaultDocName) {
        this.defaultDocName = defaultDocName;
    }

    @XmlAttribute
    public Boolean getShowProposalInXML() {
        return showProposalInXML;
    }

    public void setShowProposalInXML(Boolean showProposalInXML) {
        this.showProposalInXML = showProposalInXML;
    }

    @XmlAttribute(name = "extends")
    public String getExtendsElement() {
        return extendsElement;
    }

    public void setExtendsElement(String extendsElement) {
        this.extendsElement = extendsElement;
    }

    @XmlAttribute
    public Boolean getSupportsInbound() {
        return supportsInbound;
    }

    public void setSupportsInbound(Boolean supportsInbound) {
        this.supportsInbound = supportsInbound;
    }

    @XmlAttribute
    public String getAliasId() {
        return aliasId;
    }

    public void setAliasId(String aliasId) {
        this.aliasId = aliasId;
    }

    @XmlAttribute
    public Boolean getHiddenFromXML() {
        return hiddenFromXML;
    }

    public void setHiddenFromXML(Boolean hiddenFromXML) {
        this.hiddenFromXML = hiddenFromXML;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((aliasId == null) ? 0 : aliasId.hashCode());
        result = prime * result + ((attributeCategories == null) ? 0 : attributeCategories.hashCode());
        result = prime * result + ((caption == null) ? 0 : caption.hashCode());
        result = prime * result + ((componentValidator == null) ? 0 : componentValidator.hashCode());
        result = prime * result + ((defaultDocName == null) ? 0 : defaultDocName.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((doNotInherit == null) ? 0 : doNotInherit.hashCode());
        result = prime * result + ((extendsElement == null) ? 0 : extendsElement.hashCode());
        result = prime * result + ((hiddenFromXML == null) ? 0 : hiddenFromXML.hashCode());
        result = prime * result + ((icon == null) ? 0 : icon.hashCode());
        result = prime * result + ((image == null) ? 0 : image.hashCode());
        result = prime * result + ((isAbstract == null) ? 0 : isAbstract.hashCode());
        result = prime * result + ((localId == null) ? 0 : localId.hashCode());
        result = prime * result + ((showProposalInXML == null) ? 0 : showProposalInXML.hashCode());
        result = prime * result + ((supportsInbound == null) ? 0 : supportsInbound.hashCode());
        result = prime * result + ((xmlname == null) ? 0 : xmlname.hashCode());
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
        EditorElement other = (EditorElement) obj;
        if (aliasId == null) {
            if (other.aliasId != null)
                return false;
        } else if (!aliasId.equals(other.aliasId))
            return false;
        if (attributeCategories == null) {
            if (other.attributeCategories != null)
                return false;
        } else if (!attributeCategories.equals(other.attributeCategories))
            return false;
        if (caption == null) {
            if (other.caption != null)
                return false;
        } else if (!caption.equals(other.caption))
            return false;
        if (componentValidator == null) {
            if (other.componentValidator != null)
                return false;
        } else if (!componentValidator.equals(other.componentValidator))
            return false;
        if (defaultDocName == null) {
            if (other.defaultDocName != null)
                return false;
        } else if (!defaultDocName.equals(other.defaultDocName))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (doNotInherit == null) {
            if (other.doNotInherit != null)
                return false;
        } else if (!doNotInherit.equals(other.doNotInherit))
            return false;
        if (extendsElement == null) {
            if (other.extendsElement != null)
                return false;
        } else if (!extendsElement.equals(other.extendsElement))
            return false;
        if (hiddenFromXML == null) {
            if (other.hiddenFromXML != null)
                return false;
        } else if (!hiddenFromXML.equals(other.hiddenFromXML))
            return false;
        if (icon == null) {
            if (other.icon != null)
                return false;
        } else if (!icon.equals(other.icon))
            return false;
        if (image == null) {
            if (other.image != null)
                return false;
        } else if (!image.equals(other.image))
            return false;
        if (isAbstract == null) {
            if (other.isAbstract != null)
                return false;
        } else if (!isAbstract.equals(other.isAbstract))
            return false;
        if (localId == null) {
            if (other.localId != null)
                return false;
        } else if (!localId.equals(other.localId))
            return false;
        if (showProposalInXML == null) {
            if (other.showProposalInXML != null)
                return false;
        } else if (!showProposalInXML.equals(other.showProposalInXML))
            return false;
        if (supportsInbound == null) {
            if (other.supportsInbound != null)
                return false;
        } else if (!supportsInbound.equals(other.supportsInbound))
            return false;
        if (xmlname == null) {
            if (other.xmlname != null)
                return false;
        } else if (!xmlname.equals(other.xmlname))
            return false;
        return true;
    }

}
