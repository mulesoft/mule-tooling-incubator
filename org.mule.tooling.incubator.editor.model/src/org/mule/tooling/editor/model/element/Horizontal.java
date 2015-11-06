package org.mule.tooling.editor.model.element;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement
public class Horizontal extends BaseChildEditorElement {

    private Boolean equalWidth;
    private Boolean fillHorizontal;
    private Integer marginWidth;
    private String name;//TODO es un BaseFieldEditor?
    private List<BaseFieldEditorElement> childs;

    @XmlElementRef
    public List<BaseFieldEditorElement> getChilds() {
        if (childs == null) {
            childs = new ArrayList<BaseFieldEditorElement>();
        }
        return childs;
    }

    public void setChilds(List<BaseFieldEditorElement> childs) {
        this.childs = childs;
    }

    @XmlAttribute
    public Boolean getEqualWidth() {
        return equalWidth;
    }

    public void setEqualWidth(Boolean equalWidth) {
        this.equalWidth = equalWidth;
    }

    @XmlAttribute
    public Boolean getFillHorizontal() {
        return fillHorizontal;
    }

    public void setFillHorizontal(Boolean fillHorizontal) {
        this.fillHorizontal = fillHorizontal;
    }

    @XmlAttribute
    public Integer getMarginWidth() {
        return marginWidth;
    }

    public void setMarginWidth(Integer marginWidth) {
        this.marginWidth = marginWidth;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Horizontal [equalWidth=" + equalWidth + ", fillHorizontal=" + fillHorizontal + ", marginWidth=" + marginWidth + ", name=" + name + "]";
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((childs == null) ? 0 : childs.hashCode());
        result = prime * result + ((equalWidth == null) ? 0 : equalWidth.hashCode());
        result = prime * result + ((fillHorizontal == null) ? 0 : fillHorizontal.hashCode());
        result = prime * result + ((marginWidth == null) ? 0 : marginWidth.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Horizontal other = (Horizontal) obj;
        if (childs == null) {
            if (other.childs != null)
                return false;
        } else if (!childs.equals(other.childs))
            return false;
        if (equalWidth == null) {
            if (other.equalWidth != null)
                return false;
        } else if (!equalWidth.equals(other.equalWidth))
            return false;
        if (fillHorizontal == null) {
            if (other.fillHorizontal != null)
                return false;
        } else if (!fillHorizontal.equals(other.fillHorizontal))
            return false;
        if (marginWidth == null) {
            if (other.marginWidth != null)
                return false;
        } else if (!marginWidth.equals(other.marginWidth))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
