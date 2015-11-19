package org.mule.tooling.studio.ui.editor;

import org.apache.commons.lang.StringUtils;
import org.mule.tooling.editor.model.Alternative;
import org.mule.tooling.editor.model.CloudConnector;
import org.mule.tooling.editor.model.Component;
import org.mule.tooling.editor.model.Connector;
import org.mule.tooling.editor.model.Container;
import org.mule.tooling.editor.model.EditorElement;
import org.mule.tooling.editor.model.Endpoint;
import org.mule.tooling.editor.model.Filter;
import org.mule.tooling.editor.model.Flow;
import org.mule.tooling.editor.model.GraphicalContainer;
import org.mule.tooling.editor.model.IComponentElement;
import org.mule.tooling.editor.model.IElementVisitor;
import org.mule.tooling.editor.model.Keyword;
import org.mule.tooling.editor.model.KeywordSet;
import org.mule.tooling.editor.model.LocalRef;
import org.mule.tooling.editor.model.MultiSource;
import org.mule.tooling.editor.model.Namespace;
import org.mule.tooling.editor.model.Nested;
import org.mule.tooling.editor.model.NestedContainer;
import org.mule.tooling.editor.model.Pattern;
import org.mule.tooling.editor.model.Radio;
import org.mule.tooling.editor.model.RequiredSetAlternatives;
import org.mule.tooling.editor.model.Transformer;
import org.mule.tooling.editor.model.Wizard;
import org.mule.tooling.editor.model.element.AttributeCategory;
import org.mule.tooling.editor.model.element.BaseFieldEditorElement;
import org.mule.tooling.editor.model.element.BooleanEditor;
import org.mule.tooling.editor.model.element.Button;
import org.mule.tooling.editor.model.element.Case;
import org.mule.tooling.editor.model.element.ChildElement;
import org.mule.tooling.editor.model.element.ClassNameEditor;
import org.mule.tooling.editor.model.element.Custom;
import org.mule.tooling.editor.model.element.DateTimeEditor;
import org.mule.tooling.editor.model.element.Dummy;
import org.mule.tooling.editor.model.element.DynamicEditor;
import org.mule.tooling.editor.model.element.EditorRef;
import org.mule.tooling.editor.model.element.ElementControllerList;
import org.mule.tooling.editor.model.element.ElementControllerListNoExpression;
import org.mule.tooling.editor.model.element.ElementControllerListOfMap;
import org.mule.tooling.editor.model.element.ElementControllerMap;
import org.mule.tooling.editor.model.element.ElementControllerMapNoExpression;
import org.mule.tooling.editor.model.element.ElementQuery;
import org.mule.tooling.editor.model.element.EncodingEditor;
import org.mule.tooling.editor.model.element.EnumEditor;
import org.mule.tooling.editor.model.element.FileEditor;
import org.mule.tooling.editor.model.element.FixedAttribute;
import org.mule.tooling.editor.model.element.Group;
import org.mule.tooling.editor.model.element.Horizontal;
import org.mule.tooling.editor.model.element.IntegerEditor;
import org.mule.tooling.editor.model.element.LabelElement;
import org.mule.tooling.editor.model.element.ListEditor;
import org.mule.tooling.editor.model.element.LongEditor;
import org.mule.tooling.editor.model.element.Mode;
import org.mule.tooling.editor.model.element.ModeSwitch;
import org.mule.tooling.editor.model.element.NameEditor;
import org.mule.tooling.editor.model.element.NoOperation;
import org.mule.tooling.editor.model.element.Option;
import org.mule.tooling.editor.model.element.PasswordEditor;
import org.mule.tooling.editor.model.element.PathEditor;
import org.mule.tooling.editor.model.element.RadioBoolean;
import org.mule.tooling.editor.model.element.Regexp;
import org.mule.tooling.editor.model.element.RequiredLibraries;
import org.mule.tooling.editor.model.element.ResourceEditor;
import org.mule.tooling.editor.model.element.SoapInterceptor;
import org.mule.tooling.editor.model.element.StringEditor;
import org.mule.tooling.editor.model.element.StringMap;
import org.mule.tooling.editor.model.element.SwitchCase;
import org.mule.tooling.editor.model.element.TextEditor;
import org.mule.tooling.editor.model.element.TimeEditor;
import org.mule.tooling.editor.model.element.TypeChooser;
import org.mule.tooling.editor.model.element.UrlEditor;
import org.mule.tooling.editor.model.element.UseMetaData;
import org.mule.tooling.editor.model.element.library.Jar;
import org.mule.tooling.editor.model.element.library.LibrarySet;
import org.mule.tooling.editor.model.element.library.NativeLibrary;
import org.mule.tooling.editor.model.global.CloudConnectorMessageSource;
import org.mule.tooling.editor.model.global.Global;
import org.mule.tooling.editor.model.global.GlobalCloudConnector;
import org.mule.tooling.editor.model.global.GlobalEndpoint;
import org.mule.tooling.editor.model.global.GlobalFilter;
import org.mule.tooling.editor.model.global.GlobalTransformer;
import org.mule.tooling.editor.model.reference.ContainerRef;
import org.mule.tooling.editor.model.reference.FlowRef;
import org.mule.tooling.editor.model.reference.GlobalRef;
import org.mule.tooling.editor.model.reference.ReverseGlobalRef;

public class GetLabelsVisitor implements IElementVisitor {

    private String label;

    private String doGetLabel(IComponentElement element) {
        return element.toString();
    }

    private String doGetLabel(EditorElement element) {
        if (StringUtils.isBlank(element.getCaption())) {
            return String.format("%s: (%s)", element.getClass().getSimpleName(), element.getLocalId());
        }
        return String.format("%s: (%s) %s", element.getCaption(), element.getLocalId(), element.getClass().getSimpleName());
    }

    private String doGetLabel(BaseFieldEditorElement element) {
        if (StringUtils.isBlank(element.getCaption())) {
            return String.format("%s: (%s)", element.getName(), element.getClass().getSimpleName());
        }
        return String.format("%s: (%s) %s", element.getCaption(), element.getName(), element.getClass().getSimpleName());
    }

    @Override
    public void visit(CloudConnectorMessageSource cloudConnectorMessageSource) {
        label = String.format("CloudConnectorMessageSource (%s)", cloudConnectorMessageSource.getLocalId());
    }

    @Override
    public void visit(Global global) {
        label = doGetLabel(global);
    }

    @Override
    public void visit(GlobalCloudConnector globalCloudConnector) {
        label = doGetLabel(globalCloudConnector);
    }

    @Override
    public void visit(GlobalEndpoint globalEndpoint) {
        label = doGetLabel(globalEndpoint);
    }

    @Override
    public void visit(GlobalFilter globalFilter) {
        label = doGetLabel(globalFilter);

    }

    @Override
    public void visit(GlobalTransformer globalTransformer) {
        label = doGetLabel(globalTransformer);

    }

    @Override
    public void visit(AttributeCategory attributeCategory) {
        if (StringUtils.isBlank(attributeCategory.getCaption())) {
            label = String.format("%s: (%s)", attributeCategory.getClass().getSimpleName(), attributeCategory.getId() == null ? "" : attributeCategory.getId());
        } else {
            label = String.format("%s: (%s) %s", attributeCategory.getCaption(), attributeCategory.getId() == null ? "" : attributeCategory.getId(), attributeCategory.getClass()
                    .getSimpleName());
        }

    }

    @Override
    public void visit(BooleanEditor booleanEditor) {
        label = doGetLabel(booleanEditor);

    }

    @Override
    public void visit(Button button) {
        label = doGetLabel(button);

    }

    @Override
    public void visit(Case caseEditorElement) {
        label = doGetLabel(caseEditorElement);

    }

    @Override
    public void visit(ChildElement childElement) {
        label = doGetLabel(childElement);

    }

    @Override
    public void visit(ClassNameEditor classNameEditor) {
        label = doGetLabel(classNameEditor);

    }

    @Override
    public void visit(Custom custom) {
        label = doGetLabel(custom);

    }

    @Override
    public void visit(Dummy dummy) {
        label = doGetLabel(dummy);

    }

    @Override
    public void visit(DynamicEditor dynamicEditor) {
        label = doGetLabel(dynamicEditor);

    }

    @Override
    public void visit(EditorRef editorRef) {
        label = doGetLabel(editorRef);

    }

    @Override
    public void visit(ElementControllerList elementControllerList) {
        label = doGetLabel(elementControllerList);

    }

    @Override
    public void visit(ElementControllerListNoExpression elementControllerListNoExpression) {
        label = doGetLabel(elementControllerListNoExpression);

    }

    @Override
    public void visit(ElementControllerListOfMap elementControllerListOfMap) {
        label = doGetLabel(elementControllerListOfMap);

    }

    @Override
    public void visit(ElementControllerMap elementControllerMap) {
        label = doGetLabel(elementControllerMap);

    }

    @Override
    public void visit(ElementControllerMapNoExpression elementControllerMapNoExpression) {
        label = doGetLabel(elementControllerMapNoExpression);

    }

    @Override
    public void visit(ElementQuery elementQuery) {
        label = doGetLabel(elementQuery);

    }

    @Override
    public void visit(EncodingEditor encodingEditor) {
        label = doGetLabel(encodingEditor);

    }

    @Override
    public void visit(EnumEditor enumEditor) {
        label = doGetLabel(enumEditor);
    }

    @Override
    public void visit(FixedAttribute fixedAttribute) {
        label = doGetLabel(fixedAttribute);

    }

    @Override
    public void visit(Group group) {
        label = "Group: " + group.getId();

    }

    @Override
    public void visit(Horizontal horizontal) {
        label = doGetLabel(horizontal);

    }

    @Override
    public void visit(IntegerEditor integerEditor) {
        label = doGetLabel(integerEditor);

    }

    @Override
    public void visit(LabelElement labelElement) {
        label = doGetLabel(labelElement);

    }

    @Override
    public void visit(ListEditor listEditor) {
        label = doGetLabel(listEditor);

    }

    @Override
    public void visit(LongEditor longEditor) {
        label = doGetLabel(longEditor);

    }

    @Override
    public void visit(Mode mode) {
        label = doGetLabel(mode);

    }

    @Override
    public void visit(ModeSwitch modeSwitch) {
        label = doGetLabel(modeSwitch);

    }

    @Override
    public void visit(NameEditor nameEditor) {
        label = doGetLabel(nameEditor);

    }

    @Override
    public void visit(NativeLibrary nativeLibrary) {
        label = doGetLabel(nativeLibrary);

    }

    @Override
    public void visit(NoOperation noOperation) {
        label = doGetLabel(noOperation);

    }

    @Override
    public void visit(Option option) {
        StringBuilder buff = new StringBuilder();
        if (StringUtils.isEmpty(option.getCaption()) && StringUtils.isEmpty(option.getValue())) {
            label = doGetLabel(option);
        } else {
            if (!StringUtils.isEmpty(option.getCaption())) {
                buff.append(option.getCaption());
                buff.append(" - ");
                buff.append("[ value: ");
                buff.append(option.getValue());
                buff.append(" ]");
            } else {
                buff.append(option.getValue());
            }

            label = buff.toString();
        }

    }

    @Override
    public void visit(PasswordEditor passwordEditor) {
        label = doGetLabel(passwordEditor);

    }

    @Override
    public void visit(PathEditor pathEditor) {
        label = doGetLabel(pathEditor);

    }

    @Override
    public void visit(RadioBoolean radioBoolean) {
        label = doGetLabel(radioBoolean);

    }

    @Override
    public void visit(Regexp regexp) {
        label = doGetLabel(regexp);

    }

    @Override
    public void visit(RequiredLibraries requiredLibraries) {
        label = doGetLabel(requiredLibraries);

    }

    @Override
    public void visit(ResourceEditor resource) {
        label = doGetLabel(resource);

    }

    @Override
    public void visit(SoapInterceptor soapInterceptor) {
        label = doGetLabel(soapInterceptor);

    }

    @Override
    public void visit(StringEditor stringEditor) {
        label = doGetLabel(stringEditor);
    }

    @Override
    public void visit(StringMap stringMap) {
        label = doGetLabel(stringMap);

    }

    @Override
    public void visit(SwitchCase switchCase) {
        label = doGetLabel(switchCase);

    }

    @Override
    public void visit(TextEditor textEditor) {
        label = doGetLabel(textEditor);

    }

    @Override
    public void visit(TimeEditor timeEditor) {
        label = doGetLabel(timeEditor);

    }

    @Override
    public void visit(TypeChooser typeChooser) {
        label = doGetLabel(typeChooser);

    }

    @Override
    public void visit(UrlEditor urlEditor) {
        label = doGetLabel(urlEditor);

    }

    @Override
    public void visit(UseMetaData useMetaData) {
        label = doGetLabel(useMetaData);

    }

    @Override
    public void visit(Jar jar) {
        label = doGetLabel(jar);

    }

    @Override
    public void visit(LibrarySet librarySet) {
        label = doGetLabel(librarySet);

    }

    @Override
    public void visit(ContainerRef containerRef) {
        label = doGetLabel(containerRef);

    }

    @Override
    public void visit(FlowRef flowRef) {
        label = doGetLabel(flowRef);

    }

    @Override
    public void visit(GlobalRef globalRef) {
        label = doGetLabel(globalRef);

    }

    @Override
    public void visit(ReverseGlobalRef reverseGlobalRef) {
        label = doGetLabel(reverseGlobalRef);

    }

    @Override
    public void visit(Alternative alternative) {
        label = doGetLabel(alternative);

    }

    @Override
    public void visit(CloudConnector cloudConnector) {
        label = doGetLabel(cloudConnector);

    }

    @Override
    public void visit(Component component) {
        label = doGetLabel(component);

    }

    @Override
    public void visit(Connector connector) {
        label = doGetLabel(connector);

    }

    @Override
    public void visit(Container container) {
        label = doGetLabel(container);

    }

    @Override
    public void visit(Endpoint endpoint) {
        label = doGetLabel(endpoint);

    }

    @Override
    public void visit(Filter filter) {
        label = doGetLabel(filter);

    }

    @Override
    public void visit(Flow flow) {
        label = doGetLabel(flow);

    }

    @Override
    public void visit(GraphicalContainer graphicalContainer) {
        label = doGetLabel(graphicalContainer);

    }

    @Override
    public void visit(Keyword keyword) {
        label = keyword.getValue() + ((keyword.getWeight() > 1) ? ": Weight [" + keyword.getWeight() + "]" : "");

    }

    @Override
    public void visit(KeywordSet keywordSet) {
        label = doGetLabel(keywordSet);

    }

    @Override
    public void visit(LocalRef localRef) {
        label = doGetLabel(localRef);

    }

    @Override
    public void visit(MultiSource multiSource) {
        label = doGetLabel(multiSource);

    }

    @Override
    public void visit(Namespace namespace) {
        if (StringUtils.isEmpty(namespace.getPrefix()) && StringUtils.isEmpty(namespace.getUrl())) {
            label = "Namespace";
        } else {
            label = doGetLabel(namespace);
        }
    }

    @Override
    public void visit(Nested nested) {
        label = doGetLabel(nested);

    }

    @Override
    public void visit(NestedContainer nestedContainer) {
        label = doGetLabel(nestedContainer);

    }

    @Override
    public void visit(Pattern pattern) {
        label = doGetLabel(pattern);

    }

    @Override
    public void visit(Radio radio) {
        label = doGetLabel(radio);

    }

    @Override
    public void visit(RequiredSetAlternatives requiredSetAlternatives) {
        label = doGetLabel(requiredSetAlternatives);
    }

    @Override
    public void visit(Transformer transformer) {
        label = doGetLabel(transformer);
    }

    @Override
    public void visit(Wizard wizard) {
        label = doGetLabel(wizard);
    }

    @Override
    public void visit(FileEditor fileEditor) {
        label = doGetLabel(fileEditor);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public void visit(DateTimeEditor datetimeEditor) {
        label = doGetLabel(datetimeEditor);
        
    }

}
