package org.mule.tooling.messaging.editor.global.booleanloaders;

import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;
import org.mule.tooling.ui.modules.core.widgets.IFieldEditor;
import org.mule.tooling.ui.modules.core.widgets.editors.ILoadedValueModifier;

public abstract class Abstract3WayLoadedValueModifierTemplate implements ILoadedValueModifier {

    @Override
    public String getModifiedValue(IFieldEditor currentFieldEditor, MessageFlowNode currentNode, PropertyCollectionMap props, String originalValue) {
        if (getNestedRadioBooleanId().equals(currentFieldEditor.getHelper().getId())) {
            return haveNestedConfiguration(props).toString();
        }

        if (getGlobalRefRadioBooleanId().equals(currentFieldEditor.getHelper().getId())) {
            return haveGlobalRefConfiguration(props).toString();
        }

        if (getNoneConfigurationRadioBooleanId().equals(currentFieldEditor.getHelper().getId())) {
            Boolean noneDefinition = !(haveGlobalRefConfiguration(props) || haveNestedConfiguration(props));
            return noneDefinition.toString();
        }

        return originalValue;
    }

    protected abstract String getNestedRadioBooleanId();

    protected abstract String getGlobalRefRadioBooleanId();

    protected abstract String getNoneConfigurationRadioBooleanId();

    protected abstract Boolean haveGlobalRefConfiguration(PropertyCollectionMap props);

    protected abstract Boolean haveNestedConfiguration(PropertyCollectionMap props);
}