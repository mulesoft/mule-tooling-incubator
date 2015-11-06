package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({ CloudConnector.class, Connector.class, Container.class, Endpoint.class, Flow.class, Filter.class, Pattern.class, Transformer.class, Wizard.class })
public abstract class AbstractPaletteComponent extends EditorElement {

    private String completionProposalDocName;
    private MetaDataBehaviour metaData;
    private KeywordSet keywords;
    private RequiredSetAlternatives requiredSetAlternatives;
    private String category;// TODO Potential Enum;
    private String paletteCategory;// TODO Potential Enum;

    @XmlAttribute
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @XmlElement(name = "required-set-alternatives")
    public RequiredSetAlternatives getRequiredSetAlternatives() {
        return requiredSetAlternatives;
    }

    public void setRequiredSetAlternatives(RequiredSetAlternatives requiredSetAlternatives) {
        this.requiredSetAlternatives = requiredSetAlternatives;
    }

    @XmlElement
    public KeywordSet getKeywords() {
        return keywords;
    }

    public void setKeywords(KeywordSet keywords) {
        this.keywords = keywords;
    }

    @XmlAttribute
    public MetaDataBehaviour getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaDataBehaviour metaData) {
        this.metaData = metaData;
    }

    @XmlAttribute
    public String getCompletionProposalDocName() {
        return completionProposalDocName;
    }

    public void setCompletionProposalDocName(String completionProposalDocName) {
        this.completionProposalDocName = completionProposalDocName;
    }

    @XmlAttribute
    public String getPaletteCategory() {
        return paletteCategory;
    }

    public void setPaletteCategory(String paletteCategory) {
        this.paletteCategory = paletteCategory;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((completionProposalDocName == null) ? 0 : completionProposalDocName.hashCode());
        result = prime * result + ((keywords == null) ? 0 : keywords.hashCode());
        result = prime * result + ((metaData == null) ? 0 : metaData.hashCode());
        result = prime * result + ((paletteCategory == null) ? 0 : paletteCategory.hashCode());
        result = prime * result + ((requiredSetAlternatives == null) ? 0 : requiredSetAlternatives.hashCode());
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
        AbstractPaletteComponent other = (AbstractPaletteComponent) obj;
        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        if (completionProposalDocName == null) {
            if (other.completionProposalDocName != null)
                return false;
        } else if (!completionProposalDocName.equals(other.completionProposalDocName))
            return false;
        if (keywords == null) {
            if (other.keywords != null)
                return false;
        } else if (!keywords.equals(other.keywords))
            return false;
        if (metaData != other.metaData)
            return false;
        if (paletteCategory == null) {
            if (other.paletteCategory != null)
                return false;
        } else if (!paletteCategory.equals(other.paletteCategory))
            return false;
        if (requiredSetAlternatives == null) {
            if (other.requiredSetAlternatives != null)
                return false;
        } else if (!requiredSetAlternatives.equals(other.requiredSetAlternatives))
            return false;
        return true;
    }
}
