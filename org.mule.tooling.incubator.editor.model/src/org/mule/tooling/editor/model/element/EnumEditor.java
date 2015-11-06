package org.mule.tooling.editor.model.element;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.tooling.editor.annotation.ClassPicker;
import org.mule.tooling.editor.model.IElementVisitor;

@XmlRootElement(name = "enum")
public class EnumEditor extends BaseFieldEditorElement {

    @ClassPicker(mustImplement = org.mule.tooling.ui.modules.core.widgets.IModelUpdater.class)
    private String modelUpdater;
    // TODO should this be just the class field? why does it need 2 different fields?
    @ClassPicker(mustImplement = org.mule.tooling.ui.modules.core.widgets.editors.IValueCalculator.class)
    private String valueCalculator;
    private Boolean autoSort;
    private String transformer;
    private String optionsProvider;
    private Boolean isChildElementChooser;
    private Boolean removeEmptyOption;
    private List<Option> options;
    private String defaultAttr; // TODO hace falta?
    private String defaultValue;
    private Boolean asRadioGroup;
    private Boolean allowsCustom;
    private Boolean horizontal;
    @ClassPicker
    private String classAttribute;

    @XmlAttribute(name = "default")
    public String getDefaultAttr() {
        return defaultAttr;
    }

    public void setDefaultAttr(String defaultAttr) {
        this.defaultAttr = defaultAttr;
    }

    @XmlAttribute
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @XmlAttribute
    public String getModelUpdater() {
        return modelUpdater;
    }

    public void setModelUpdater(String modelUpdater) {
        this.modelUpdater = modelUpdater;
    }

    @XmlAttribute
    public String getValueCalculator() {
        return valueCalculator;
    }

    public void setValueCalculator(String valueCalculator) {
        this.valueCalculator = valueCalculator;
    }

    @XmlAttribute
    public Boolean getAutoSort() {
        return autoSort;
    }

    public void setAutoSort(Boolean autoSort) {
        this.autoSort = autoSort;
    }

    @XmlAttribute
    public String getTransformer() {
        return transformer;
    }

    public void setTransformer(String transformer) {
        this.transformer = transformer;
    }

    @XmlAttribute
    public String getOptionsProvider() {
        return optionsProvider;
    }

    public void setOptionsProvider(String optionsProvider) {
        this.optionsProvider = optionsProvider;
    }

    @XmlAttribute
    public Boolean getIsChildElementChooser() {
        return isChildElementChooser;
    }

    public void setIsChildElementChooser(Boolean isChildElementChooser) {
        this.isChildElementChooser = isChildElementChooser;
    }

    @Override
    public String toString() {
        return "EnumEditor [modelUpdater=" + modelUpdater + ", valueCalculator=" + valueCalculator + ", autoSort=" + autoSort + ", transformer=" + transformer
                + ", optionsProvider=" + optionsProvider + ", isChildElementChooser=" + isChildElementChooser + ", getName()=" + getName() + ", getCaption()=" + getCaption()
                + ", getDescription()=" + getDescription() + "]";
    }

    @XmlElementRef
    public List<Option> getOptions() {
        if (options == null) {
            options = new ArrayList<Option>();
        }
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public Boolean getRemoveEmptyOption() {
        return removeEmptyOption;
    }

    public void setRemoveEmptyOption(Boolean removeEmptyOption) {
        this.removeEmptyOption = removeEmptyOption;
    }

    @XmlAttribute
    public Boolean getAsRadioGroup() {
        return asRadioGroup;
    }

    public void setAsRadioGroup(Boolean asRadioGroup) {
        this.asRadioGroup = asRadioGroup;
    }

    @XmlAttribute
    public Boolean getAllowsCustom() {
        return allowsCustom;
    }

    public void setAllowsCustom(Boolean allowsCustom) {
        this.allowsCustom = allowsCustom;
    }

    @XmlAttribute(name = "class")
    public String getClassAttribute() {
        return classAttribute;
    }

    public void setClassAttribute(String classAttribute) {
        this.classAttribute = classAttribute;
    }

    @XmlAttribute
    public Boolean getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(Boolean horizontal) {
        this.horizontal = horizontal;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((allowsCustom == null) ? 0 : allowsCustom.hashCode());
        result = prime * result + ((asRadioGroup == null) ? 0 : asRadioGroup.hashCode());
        result = prime * result + ((autoSort == null) ? 0 : autoSort.hashCode());
        result = prime * result + ((classAttribute == null) ? 0 : classAttribute.hashCode());
        result = prime * result + ((defaultAttr == null) ? 0 : defaultAttr.hashCode());
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((horizontal == null) ? 0 : horizontal.hashCode());
        result = prime * result + ((isChildElementChooser == null) ? 0 : isChildElementChooser.hashCode());
        result = prime * result + ((modelUpdater == null) ? 0 : modelUpdater.hashCode());
        result = prime * result + ((options == null) ? 0 : options.hashCode());
        result = prime * result + ((optionsProvider == null) ? 0 : optionsProvider.hashCode());
        result = prime * result + ((removeEmptyOption == null) ? 0 : removeEmptyOption.hashCode());
        result = prime * result + ((transformer == null) ? 0 : transformer.hashCode());
        result = prime * result + ((valueCalculator == null) ? 0 : valueCalculator.hashCode());
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
        EnumEditor other = (EnumEditor) obj;
        if (allowsCustom == null) {
            if (other.allowsCustom != null)
                return false;
        } else if (!allowsCustom.equals(other.allowsCustom))
            return false;
        if (asRadioGroup == null) {
            if (other.asRadioGroup != null)
                return false;
        } else if (!asRadioGroup.equals(other.asRadioGroup))
            return false;
        if (autoSort == null) {
            if (other.autoSort != null)
                return false;
        } else if (!autoSort.equals(other.autoSort))
            return false;
        if (classAttribute == null) {
            if (other.classAttribute != null)
                return false;
        } else if (!classAttribute.equals(other.classAttribute))
            return false;
        if (defaultAttr == null) {
            if (other.defaultAttr != null)
                return false;
        } else if (!defaultAttr.equals(other.defaultAttr))
            return false;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (horizontal == null) {
            if (other.horizontal != null)
                return false;
        } else if (!horizontal.equals(other.horizontal))
            return false;
        if (isChildElementChooser == null) {
            if (other.isChildElementChooser != null)
                return false;
        } else if (!isChildElementChooser.equals(other.isChildElementChooser))
            return false;
        if (modelUpdater == null) {
            if (other.modelUpdater != null)
                return false;
        } else if (!modelUpdater.equals(other.modelUpdater))
            return false;
        if (options == null) {
            if (other.options != null)
                return false;
        } else if (!options.equals(other.options))
            return false;
        if (optionsProvider == null) {
            if (other.optionsProvider != null)
                return false;
        } else if (!optionsProvider.equals(other.optionsProvider))
            return false;
        if (removeEmptyOption == null) {
            if (other.removeEmptyOption != null)
                return false;
        } else if (!removeEmptyOption.equals(other.removeEmptyOption))
            return false;
        if (transformer == null) {
            if (other.transformer != null)
                return false;
        } else if (!transformer.equals(other.transformer))
            return false;
        if (valueCalculator == null) {
            if (other.valueCalculator != null)
                return false;
        } else if (!valueCalculator.equals(other.valueCalculator))
            return false;
        return true;
    }

}
