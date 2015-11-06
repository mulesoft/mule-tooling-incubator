package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.annotation.ClassPicker;
import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "text")
public class TextEditor extends BaseFieldEditorElement {

    private Boolean wrapWithCDATA;
    private Integer textAreaHeight;
    private Integer textAreaWidth;
    private String language;
    private Boolean isToElement;
    @ClassPicker(mustImplement = org.mule.tooling.ui.modules.core.widgets.editors.TextViewerCreator.class)
    private String customTextViewerCreator;
    private String nestedName;

    @Override
    public String toString() {
        return "TextEditor [getName()=" + getName() + ", getCaption()=" + getCaption() + ", getDescription()=" + getDescription() + "]";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public Boolean getWrapWithCDATA() {
        return wrapWithCDATA;
    }

    public void setWrapWithCDATA(Boolean wrapWithCDATA) {
        this.wrapWithCDATA = wrapWithCDATA;
    }

    @XmlAttribute(name = "textAreaHeight")
    public Integer getTextAreaHeight() {
        return textAreaHeight;
    }

    public void setTextAreaHeight(Integer textAreaHeight) {
        this.textAreaHeight = textAreaHeight;
    }

    @XmlAttribute(name = "textAreaWidth")
    public Integer getTextAreaWidth() {
        return textAreaWidth;
    }

    public void setTextAreaWidth(Integer textAreaWidth) {
        this.textAreaWidth = textAreaWidth;
    }

    @XmlAttribute
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @XmlAttribute
    public Boolean getIsToElement() {
        return isToElement;
    }

    public void setIsToElement(Boolean isToElement) {
        this.isToElement = isToElement;
    }

    public String getCustomTextViewerCreator() {
        return customTextViewerCreator;
    }

    public void setCustomTextViewerCreator(String customTextViewerCreator) {
        this.customTextViewerCreator = customTextViewerCreator;
    }

    @XmlAttribute
    public String getNestedName() {
        return nestedName;
    }

    public void setNestedName(String nestedName) {
        this.nestedName = nestedName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((customTextViewerCreator == null) ? 0 : customTextViewerCreator.hashCode());
        result = prime * result + ((isToElement == null) ? 0 : isToElement.hashCode());
        result = prime * result + ((language == null) ? 0 : language.hashCode());
        result = prime * result + ((nestedName == null) ? 0 : nestedName.hashCode());
        result = prime * result + ((textAreaHeight == null) ? 0 : textAreaHeight.hashCode());
        result = prime * result + ((textAreaWidth == null) ? 0 : textAreaWidth.hashCode());
        result = prime * result + ((wrapWithCDATA == null) ? 0 : wrapWithCDATA.hashCode());
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
        TextEditor other = (TextEditor) obj;
        if (customTextViewerCreator == null) {
            if (other.customTextViewerCreator != null)
                return false;
        } else if (!customTextViewerCreator.equals(other.customTextViewerCreator))
            return false;
        if (isToElement == null) {
            if (other.isToElement != null)
                return false;
        } else if (!isToElement.equals(other.isToElement))
            return false;
        if (language == null) {
            if (other.language != null)
                return false;
        } else if (!language.equals(other.language))
            return false;
        if (nestedName == null) {
            if (other.nestedName != null)
                return false;
        } else if (!nestedName.equals(other.nestedName))
            return false;
        if (textAreaHeight == null) {
            if (other.textAreaHeight != null)
                return false;
        } else if (!textAreaHeight.equals(other.textAreaHeight))
            return false;
        if (textAreaWidth == null) {
            if (other.textAreaWidth != null)
                return false;
        } else if (!textAreaWidth.equals(other.textAreaWidth))
            return false;
        if (wrapWithCDATA == null) {
            if (other.wrapWithCDATA != null)
                return false;
        } else if (!wrapWithCDATA.equals(other.wrapWithCDATA))
            return false;
        return true;
    }

}
