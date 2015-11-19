package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.mule.tooling.editor.annotation.ClassPicker;
import org.mule.tooling.editor.model.Radio;
import org.mule.tooling.editor.model.reference.AbstractRef;
import org.mule.tooling.editor.model.ModeType;

@XmlSeeAlso({ AbstractRef.class, BaseStringEditor.class, BooleanEditor.class, ChildElement.class, ClassNameEditor.class, Custom.class, DateTimeEditor.class, Dummy.class,
        DynamicEditor.class, EncodingEditor.class, EnumEditor.class, FixedAttribute.class, IntegerEditor.class, LabelElement.class, ListEditor.class, LongEditor.class,
        ModeSwitch.class, NameEditor.class, ElementQuery.class, Radio.class, RadioBoolean.class, Regexp.class, ResourceEditor.class, SoapInterceptor.class, TextEditor.class,
        TypeChooser.class })
public abstract class BaseFieldEditorElement extends BaseChildEditorElement {

    private String name;
    private Boolean required;
    private String recalcWhen;
    private Boolean alwaysFill;
    private Boolean visibleInDialog;
    private String alternativeTo;
    private Boolean unchangeable;// TODO Used in boolean,enum,integer,string
    private Boolean hiddenForUser;
    private Boolean editable;// TODO ....sirve de algo??
    private Boolean tiny;// TODO Solo lo usa BooleanEditor
    private Integer span;
    private String xsdType;
    private String javaType;
    private Boolean requiredForDataSense;
    private String saveAs;// TODO es lo mismo que xmlname, pero a nivel de attributo
    private ModeType mode;
    private String controlled;
    private String groupIn;
    private Boolean transientField;
    private Boolean indented;
    private Boolean supportsExpressions;
    private String topAnchor;
    private String bottomAnchor;
    private Boolean loadLater;
    private Boolean fillLine;
    private Boolean fillWhenNotEmpty;
    private Boolean hideDisabled;
    private Boolean hideInChild;
    private Boolean storeIndependently;// TODO Solo se usa en boolean...
    private Integer priority;

    @ClassPicker(mustImplement = org.mule.tooling.ui.modules.core.widgets.IValidator.class)
    private String customValidator;

    @ClassPicker(mustImplement = org.mule.tooling.ui.modules.core.widgets.meta.AttributesPersistenceTransformer.class)
    private String persistenceTransformer;

    @ClassPicker(mustExtend = org.mule.tooling.ui.modules.core.widgets.meta.AbstractValuePersistence.class)
    private String valuePersistence;
    private String valuePersistenceRequires;

    @ClassPicker(mustImplement = org.mule.tooling.ui.modules.core.widgets.meta.IDialogAction.class)
    private String actionListener;

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    @XmlAttribute
    public String getCustomValidator() {
        return customValidator;
    }

    public void setCustomValidator(String customValidator) {
        this.customValidator = customValidator;
    }

    @XmlAttribute
    public String getRecalcWhen() {
        return recalcWhen;
    }

    public void setRecalcWhen(String recalcWhen) {
        this.recalcWhen = recalcWhen;
    }

    @XmlAttribute
    public Boolean getAlwaysFill() {
        return alwaysFill;
    }

    public void setAlwaysFill(Boolean alwaysFill) {
        this.alwaysFill = alwaysFill;
    }

    @XmlAttribute
    public Boolean getVisibleInDialog() {
        return visibleInDialog;
    }

    public void setVisibleInDialog(Boolean visibleInDialog) {
        this.visibleInDialog = visibleInDialog;
    }

    @XmlAttribute
    public String getAlternativeTo() {
        return alternativeTo;
    }

    public void setAlternativeTo(String alternativeTo) {
        this.alternativeTo = alternativeTo;
    }

    @XmlAttribute
    public Boolean getUnchangeable() {
        return unchangeable;
    }

    public void setUnchangeable(Boolean unchangeable) {
        this.unchangeable = unchangeable;
    }

    @XmlAttribute
    public Boolean getHiddenForUser() {
        return hiddenForUser;
    }

    public void setHiddenForUser(Boolean hiddenForUser) {
        this.hiddenForUser = hiddenForUser;
    }

    @XmlAttribute
    public String getPersistenceTransformer() {
        return persistenceTransformer;
    }

    public void setPersistenceTransformer(String persistenceTransformer) {
        this.persistenceTransformer = persistenceTransformer;
    }

    @XmlAttribute
    public String getValuePersistence() {
        return valuePersistence;
    }

    public void setValuePersistence(String valuePersistence) {
        this.valuePersistence = valuePersistence;
    }

    @XmlAttribute
    public String getValuePersistenceRequires() {
        return valuePersistenceRequires;
    }

    public void setValuePersistenceRequires(String valuePersistenceRequires) {
        this.valuePersistenceRequires = valuePersistenceRequires;
    }

    @XmlAttribute
    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    @XmlAttribute
    public Boolean getTiny() {
        return tiny;
    }

    public void setTiny(Boolean tiny) {
        this.tiny = tiny;
    }

    @XmlAttribute
    public Integer getSpan() {
        return span;
    }

    public void setSpan(Integer span) {
        this.span = span;
    }

    @XmlAttribute
    public String getXsdType() {
        return xsdType;
    }

    public void setXsdType(String xsdType) {
        this.xsdType = xsdType;
    }

    @XmlAttribute
    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    @XmlAttribute
    public Boolean getRequiredForDataSense() {
        return requiredForDataSense;
    }

    public void setRequiredForDataSense(Boolean requiredForDataSense) {
        this.requiredForDataSense = requiredForDataSense;
    }

    @XmlAttribute
    public String getSaveAs() {
        return saveAs;
    }

    public void setSaveAs(String saveAs) {
        this.saveAs = saveAs;
    }

    @XmlAttribute
    public ModeType getMode() {
        return mode;
    }

    public void setMode(ModeType mode) {
        this.mode = mode;
    }

    @XmlAttribute
    public String getControlled() {
        return controlled;
    }

    public void setControlled(String controlled) {
        this.controlled = controlled;
    }

    @XmlAttribute
    public String getGroupIn() {
        return groupIn;
    }

    public void setGroupIn(String groupIn) {
        this.groupIn = groupIn;
    }

    @XmlAttribute(name = "transient")
    public Boolean getTransientField() {
        return transientField;
    }

    public void setTransientField(Boolean transientField) {
        this.transientField = transientField;
    }

    @XmlAttribute
    public Boolean getIndented() {
        return indented;
    }

    public void setIndented(Boolean indented) {
        this.indented = indented;
    }

    @XmlAttribute
    public Boolean getSupportsExpressions() {
        return supportsExpressions;
    }

    public void setSupportsExpressions(Boolean supportsExpressions) {
        this.supportsExpressions = supportsExpressions;
    }

    @XmlAttribute
    public String getTopAnchor() {
        return topAnchor;
    }

    public void setTopAnchor(String topAnchor) {
        this.topAnchor = topAnchor;
    }

    @XmlAttribute
    public String getBottomAnchor() {
        return bottomAnchor;
    }

    public void setBottomAnchor(String bottomAnchor) {
        this.bottomAnchor = bottomAnchor;
    }

    @XmlAttribute
    public String getActionListener() {
        return actionListener;
    }

    public void setActionListener(String actionListener) {
        this.actionListener = actionListener;
    }

    @XmlAttribute
    public Boolean getLoadLater() {
        return loadLater;
    }

    public void setLoadLater(Boolean loadLater) {
        this.loadLater = loadLater;
    }

    @XmlAttribute
    public Boolean getFillWhenNotEmpty() {
        return fillWhenNotEmpty;
    }

    public void setFillWhenNotEmpty(Boolean fillWhenNotEmpty) {
        this.fillWhenNotEmpty = fillWhenNotEmpty;
    }

    @XmlAttribute
    public Boolean getFillLine() {
        return fillLine;
    }

    public void setFillLine(Boolean fillLine) {
        this.fillLine = fillLine;
    }

    @XmlAttribute
    public Boolean getHideDisabled() {
        return hideDisabled;
    }

    public void setHideDisabled(Boolean hideDisabled) {
        this.hideDisabled = hideDisabled;
    }

    @XmlAttribute
    public Boolean getHideInChild() {
        return hideInChild;
    }

    public void setHideInChild(Boolean hideInChild) {
        this.hideInChild = hideInChild;
    }

    @XmlAttribute
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @XmlAttribute
    public Boolean getStoreIndependently() {
        return storeIndependently;
    }

    public void setStoreIndependently(Boolean storeIndependently) {
        this.storeIndependently = storeIndependently;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((actionListener == null) ? 0 : actionListener.hashCode());
        result = prime * result + ((alternativeTo == null) ? 0 : alternativeTo.hashCode());
        result = prime * result + ((alwaysFill == null) ? 0 : alwaysFill.hashCode());
        result = prime * result + ((bottomAnchor == null) ? 0 : bottomAnchor.hashCode());
        result = prime * result + ((controlled == null) ? 0 : controlled.hashCode());
        result = prime * result + ((customValidator == null) ? 0 : customValidator.hashCode());
        result = prime * result + ((editable == null) ? 0 : editable.hashCode());
        result = prime * result + ((fillLine == null) ? 0 : fillLine.hashCode());
        result = prime * result + ((fillWhenNotEmpty == null) ? 0 : fillWhenNotEmpty.hashCode());
        result = prime * result + ((groupIn == null) ? 0 : groupIn.hashCode());
        result = prime * result + ((hiddenForUser == null) ? 0 : hiddenForUser.hashCode());
        result = prime * result + ((hideDisabled == null) ? 0 : hideDisabled.hashCode());
        result = prime * result + ((hideInChild == null) ? 0 : hideInChild.hashCode());
        result = prime * result + ((indented == null) ? 0 : indented.hashCode());
        result = prime * result + ((javaType == null) ? 0 : javaType.hashCode());
        result = prime * result + ((loadLater == null) ? 0 : loadLater.hashCode());
        result = prime * result + ((mode == null) ? 0 : mode.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((persistenceTransformer == null) ? 0 : persistenceTransformer.hashCode());
        result = prime * result + ((priority == null) ? 0 : priority.hashCode());
        result = prime * result + ((recalcWhen == null) ? 0 : recalcWhen.hashCode());
        result = prime * result + ((required == null) ? 0 : required.hashCode());
        result = prime * result + ((requiredForDataSense == null) ? 0 : requiredForDataSense.hashCode());
        result = prime * result + ((saveAs == null) ? 0 : saveAs.hashCode());
        result = prime * result + ((span == null) ? 0 : span.hashCode());
        result = prime * result + ((storeIndependently == null) ? 0 : storeIndependently.hashCode());
        result = prime * result + ((supportsExpressions == null) ? 0 : supportsExpressions.hashCode());
        result = prime * result + ((tiny == null) ? 0 : tiny.hashCode());
        result = prime * result + ((topAnchor == null) ? 0 : topAnchor.hashCode());
        result = prime * result + ((transientField == null) ? 0 : transientField.hashCode());
        result = prime * result + ((unchangeable == null) ? 0 : unchangeable.hashCode());
        result = prime * result + ((valuePersistence == null) ? 0 : valuePersistence.hashCode());
        result = prime * result + ((valuePersistenceRequires == null) ? 0 : valuePersistenceRequires.hashCode());
        result = prime * result + ((visibleInDialog == null) ? 0 : visibleInDialog.hashCode());
        result = prime * result + ((xsdType == null) ? 0 : xsdType.hashCode());
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
        BaseFieldEditorElement other = (BaseFieldEditorElement) obj;
        if (actionListener == null) {
            if (other.actionListener != null)
                return false;
        } else if (!actionListener.equals(other.actionListener))
            return false;
        if (alternativeTo == null) {
            if (other.alternativeTo != null)
                return false;
        } else if (!alternativeTo.equals(other.alternativeTo))
            return false;
        if (alwaysFill == null) {
            if (other.alwaysFill != null)
                return false;
        } else if (!alwaysFill.equals(other.alwaysFill))
            return false;
        if (bottomAnchor == null) {
            if (other.bottomAnchor != null)
                return false;
        } else if (!bottomAnchor.equals(other.bottomAnchor))
            return false;
        if (controlled == null) {
            if (other.controlled != null)
                return false;
        } else if (!controlled.equals(other.controlled))
            return false;
        if (customValidator == null) {
            if (other.customValidator != null)
                return false;
        } else if (!customValidator.equals(other.customValidator))
            return false;
        if (editable == null) {
            if (other.editable != null)
                return false;
        } else if (!editable.equals(other.editable))
            return false;
        if (fillLine == null) {
            if (other.fillLine != null)
                return false;
        } else if (!fillLine.equals(other.fillLine))
            return false;
        if (fillWhenNotEmpty == null) {
            if (other.fillWhenNotEmpty != null)
                return false;
        } else if (!fillWhenNotEmpty.equals(other.fillWhenNotEmpty))
            return false;
        if (groupIn == null) {
            if (other.groupIn != null)
                return false;
        } else if (!groupIn.equals(other.groupIn))
            return false;
        if (hiddenForUser == null) {
            if (other.hiddenForUser != null)
                return false;
        } else if (!hiddenForUser.equals(other.hiddenForUser))
            return false;
        if (hideDisabled == null) {
            if (other.hideDisabled != null)
                return false;
        } else if (!hideDisabled.equals(other.hideDisabled))
            return false;
        if (hideInChild == null) {
            if (other.hideInChild != null)
                return false;
        } else if (!hideInChild.equals(other.hideInChild))
            return false;
        if (indented == null) {
            if (other.indented != null)
                return false;
        } else if (!indented.equals(other.indented))
            return false;
        if (javaType == null) {
            if (other.javaType != null)
                return false;
        } else if (!javaType.equals(other.javaType))
            return false;
        if (loadLater == null) {
            if (other.loadLater != null)
                return false;
        } else if (!loadLater.equals(other.loadLater))
            return false;
        if (mode != other.mode)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (persistenceTransformer == null) {
            if (other.persistenceTransformer != null)
                return false;
        } else if (!persistenceTransformer.equals(other.persistenceTransformer))
            return false;
        if (priority == null) {
            if (other.priority != null)
                return false;
        } else if (!priority.equals(other.priority))
            return false;
        if (recalcWhen == null) {
            if (other.recalcWhen != null)
                return false;
        } else if (!recalcWhen.equals(other.recalcWhen))
            return false;
        if (required == null) {
            if (other.required != null)
                return false;
        } else if (!required.equals(other.required))
            return false;
        if (requiredForDataSense == null) {
            if (other.requiredForDataSense != null)
                return false;
        } else if (!requiredForDataSense.equals(other.requiredForDataSense))
            return false;
        if (saveAs == null) {
            if (other.saveAs != null)
                return false;
        } else if (!saveAs.equals(other.saveAs))
            return false;
        if (span == null) {
            if (other.span != null)
                return false;
        } else if (!span.equals(other.span))
            return false;
        if (storeIndependently == null) {
            if (other.storeIndependently != null)
                return false;
        } else if (!storeIndependently.equals(other.storeIndependently))
            return false;
        if (supportsExpressions == null) {
            if (other.supportsExpressions != null)
                return false;
        } else if (!supportsExpressions.equals(other.supportsExpressions))
            return false;
        if (tiny == null) {
            if (other.tiny != null)
                return false;
        } else if (!tiny.equals(other.tiny))
            return false;
        if (topAnchor == null) {
            if (other.topAnchor != null)
                return false;
        } else if (!topAnchor.equals(other.topAnchor))
            return false;
        if (transientField == null) {
            if (other.transientField != null)
                return false;
        } else if (!transientField.equals(other.transientField))
            return false;
        if (unchangeable == null) {
            if (other.unchangeable != null)
                return false;
        } else if (!unchangeable.equals(other.unchangeable))
            return false;
        if (valuePersistence == null) {
            if (other.valuePersistence != null)
                return false;
        } else if (!valuePersistence.equals(other.valuePersistence))
            return false;
        if (valuePersistenceRequires == null) {
            if (other.valuePersistenceRequires != null)
                return false;
        } else if (!valuePersistenceRequires.equals(other.valuePersistenceRequires))
            return false;
        if (visibleInDialog == null) {
            if (other.visibleInDialog != null)
                return false;
        } else if (!visibleInDialog.equals(other.visibleInDialog))
            return false;
        if (xsdType == null) {
            if (other.xsdType != null)
                return false;
        } else if (!xsdType.equals(other.xsdType))
            return false;
        return true;
    }

}
