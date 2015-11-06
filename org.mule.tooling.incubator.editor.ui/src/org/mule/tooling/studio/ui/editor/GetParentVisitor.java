package org.mule.tooling.studio.ui.editor;

import java.util.List;

import org.mule.tooling.editor.model.AbstractEditorElement;
import org.mule.tooling.editor.model.Alternative;
import org.mule.tooling.editor.model.CloudConnector;
import org.mule.tooling.editor.model.Component;
import org.mule.tooling.editor.model.Connector;
import org.mule.tooling.editor.model.Container;
import org.mule.tooling.editor.model.Endpoint;
import org.mule.tooling.editor.model.Filter;
import org.mule.tooling.editor.model.Flow;
import org.mule.tooling.editor.model.GraphicalContainer;
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
import org.mule.tooling.editor.model.element.BooleanEditor;
import org.mule.tooling.editor.model.element.Button;
import org.mule.tooling.editor.model.element.Case;
import org.mule.tooling.editor.model.element.ChildElement;
import org.mule.tooling.editor.model.element.ClassNameEditor;
import org.mule.tooling.editor.model.element.Custom;
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

public class GetParentVisitor implements IElementVisitor {

    private Object parent;
    final private Object child;

    public Object getParent() {
        return parent;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }

    public GetParentVisitor(Object child) {
        this.child = child;
    }

    private void checkIfIsParent(AbstractEditorElement element) {
        GetChildVisitor visitor = new GetChildVisitor();
        element.accept(visitor);
        List<Object> childs = visitor.getChilds();
        if (childs.contains(child)) {
            parent = element;
        } else {
            for (Object child : childs) {
                AbstractEditorElement editorElement = (AbstractEditorElement) child;
                editorElement.accept(this);
            }
        }
    }

    @Override
    public void visit(CloudConnectorMessageSource cloudConnectorMessageSource) {
        checkIfIsParent(cloudConnectorMessageSource);
    }

    @Override
    public void visit(Global global) {
        checkIfIsParent(global);
    }

    @Override
    public void visit(GlobalCloudConnector globalCloudConnector) {
        checkIfIsParent(globalCloudConnector);
    }

    @Override
    public void visit(GlobalEndpoint globalEndpoint) {
        checkIfIsParent(globalEndpoint);
    }

    @Override
    public void visit(GlobalFilter globalFilter) {
        checkIfIsParent(globalFilter);
    }

    @Override
    public void visit(GlobalTransformer globalTransformer) {
        checkIfIsParent(globalTransformer);
    }

    @Override
    public void visit(AttributeCategory attributeCategory) {
        checkIfIsParent(attributeCategory);
    }

    @Override
    public void visit(BooleanEditor booleanEditor) {
        checkIfIsParent(booleanEditor);
    }

    @Override
    public void visit(Button button) {
        checkIfIsParent(button);
    }

    @Override
    public void visit(Case caseEditorElement) {
        checkIfIsParent(caseEditorElement);
    }

    @Override
    public void visit(ChildElement childElement) {
        checkIfIsParent(childElement);
    }

    @Override
    public void visit(ClassNameEditor classNameEditor) {
        checkIfIsParent(classNameEditor);
    }

    @Override
    public void visit(Custom custom) {
        checkIfIsParent(custom);
    }

    @Override
    public void visit(Dummy dummy) {
        checkIfIsParent(dummy);
    }

    @Override
    public void visit(DynamicEditor dynamicEditor) {
        checkIfIsParent(dynamicEditor);
    }

    @Override
    public void visit(EditorRef editorRef) {
        checkIfIsParent(editorRef);
    }

    @Override
    public void visit(ElementControllerList elementControllerList) {
        checkIfIsParent(elementControllerList);
    }

    @Override
    public void visit(ElementControllerListNoExpression elementControllerListNoExpression) {
        checkIfIsParent(elementControllerListNoExpression);
    }

    @Override
    public void visit(ElementControllerListOfMap elementControllerListOfMap) {
        checkIfIsParent(elementControllerListOfMap);
    }

    @Override
    public void visit(ElementControllerMap elementControllerMap) {
        checkIfIsParent(elementControllerMap);
    }

    @Override
    public void visit(ElementControllerMapNoExpression elementControllerMapNoExpression) {
        checkIfIsParent(elementControllerMapNoExpression);
    }

    @Override
    public void visit(ElementQuery elementQuery) {
        checkIfIsParent(elementQuery);
    }

    @Override
    public void visit(EncodingEditor encodingEditor) {
        checkIfIsParent(encodingEditor);
    }

    @Override
    public void visit(EnumEditor enumEditor) {
        checkIfIsParent(enumEditor);
    }

    @Override
    public void visit(FileEditor fileEditor) {
        checkIfIsParent(fileEditor);
    }

    @Override
    public void visit(FixedAttribute fixedAttribute) {
        checkIfIsParent(fixedAttribute);
    }

    @Override
    public void visit(Group group) {
        checkIfIsParent(group);
    }

    @Override
    public void visit(Horizontal horizontal) {
        checkIfIsParent(horizontal);
    }

    @Override
    public void visit(IntegerEditor integerEditor) {
        checkIfIsParent(integerEditor);
    }

    @Override
    public void visit(LabelElement labelElement) {
        checkIfIsParent(labelElement);
    }

    @Override
    public void visit(ListEditor listEditor) {
        checkIfIsParent(listEditor);
    }

    @Override
    public void visit(LongEditor longEditor) {
        checkIfIsParent(longEditor);
    }

    @Override
    public void visit(Mode mode) {
        checkIfIsParent(mode);
    }

    @Override
    public void visit(ModeSwitch modeSwitch) {
        checkIfIsParent(modeSwitch);
    }

    @Override
    public void visit(NameEditor nameEditor) {
        checkIfIsParent(nameEditor);
    }

    @Override
    public void visit(NativeLibrary nativeLibrary) {
        checkIfIsParent(nativeLibrary);
    }

    @Override
    public void visit(NoOperation noOperation) {
        checkIfIsParent(noOperation);
    }

    @Override
    public void visit(Option option) {
        checkIfIsParent(option);
    }

    @Override
    public void visit(PasswordEditor passwordEditor) {
        checkIfIsParent(passwordEditor);
    }

    @Override
    public void visit(PathEditor pathEditor) {
        checkIfIsParent(pathEditor);
    }

    @Override
    public void visit(RadioBoolean radioBoolean) {
        checkIfIsParent(radioBoolean);
    }

    @Override
    public void visit(Regexp regexp) {
        checkIfIsParent(regexp);
    }

    @Override
    public void visit(RequiredLibraries requiredLibraries) {
        checkIfIsParent(requiredLibraries);
    }

    @Override
    public void visit(ResourceEditor resource) {
        checkIfIsParent(resource);
    }

    @Override
    public void visit(SoapInterceptor soapInterceptor) {
        checkIfIsParent(soapInterceptor);
    }

    @Override
    public void visit(StringEditor stringEditor) {
        checkIfIsParent(stringEditor);
    }

    @Override
    public void visit(StringMap stringMap) {
        checkIfIsParent(stringMap);
    }

    @Override
    public void visit(SwitchCase switchCase) {
        checkIfIsParent(switchCase);
    }

    @Override
    public void visit(TextEditor textEditor) {
        checkIfIsParent(textEditor);
    }

    @Override
    public void visit(TimeEditor timeEditor) {
        checkIfIsParent(timeEditor);
    }

    @Override
    public void visit(TypeChooser typeChooser) {
        checkIfIsParent(typeChooser);

    }

    @Override
    public void visit(UrlEditor urlEditor) {
        checkIfIsParent(urlEditor);
    }

    @Override
    public void visit(UseMetaData useMetaData) {
        checkIfIsParent(useMetaData);
    }

    @Override
    public void visit(Jar jar) {
        checkIfIsParent(jar);
    }

    @Override
    public void visit(LibrarySet librarySet) {
        checkIfIsParent(librarySet);
    }

    @Override
    public void visit(ContainerRef containerRef) {
        checkIfIsParent(containerRef);
    }

    @Override
    public void visit(FlowRef flowRef) {
        checkIfIsParent(flowRef);
    }

    @Override
    public void visit(GlobalRef globalRef) {
        checkIfIsParent(globalRef);
    }

    @Override
    public void visit(ReverseGlobalRef reverseGlobalRef) {
        checkIfIsParent(reverseGlobalRef);
    }

    @Override
    public void visit(Alternative alternative) {
        checkIfIsParent(alternative);
    }

    @Override
    public void visit(CloudConnector cloudConnector) {
        checkIfIsParent(cloudConnector);
    }

    @Override
    public void visit(Component component) {
        checkIfIsParent(component);
    }

    @Override
    public void visit(Connector connector) {
        checkIfIsParent(connector);
    }

    @Override
    public void visit(Container container) {
        checkIfIsParent(container);
    }

    @Override
    public void visit(Endpoint endpoint) {
        checkIfIsParent(endpoint);
    }

    @Override
    public void visit(Filter filter) {
        checkIfIsParent(filter);
    }

    @Override
    public void visit(Flow flow) {
        checkIfIsParent(flow);
    }

    @Override
    public void visit(GraphicalContainer graphicalContainer) {
        checkIfIsParent(graphicalContainer);
    }

    @Override
    public void visit(Keyword keyword) {
        checkIfIsParent(keyword);
    }

    @Override
    public void visit(KeywordSet keywordSet) {
        checkIfIsParent(keywordSet);
    }

    @Override
    public void visit(LocalRef localRef) {
        checkIfIsParent(localRef);
    }

    @Override
    public void visit(MultiSource multiSource) {
        checkIfIsParent(multiSource);
    }

    @Override
    public void visit(Namespace namespace) {
        checkIfIsParent(namespace);
    }

    @Override
    public void visit(Nested nested) {
        checkIfIsParent(nested);
    }

    @Override
    public void visit(NestedContainer nestedContainer) {
        checkIfIsParent(nestedContainer);
    }

    @Override
    public void visit(Pattern pattern) {
        checkIfIsParent(pattern);
    }

    @Override
    public void visit(Radio radio) {
        checkIfIsParent(radio);
    }

    @Override
    public void visit(RequiredSetAlternatives requiredSetAlternatives) {
        checkIfIsParent(requiredSetAlternatives);
    }

    @Override
    public void visit(Transformer transformer) {
        checkIfIsParent(transformer);
    }

    @Override
    public void visit(Wizard wizard) {
        checkIfIsParent(wizard);
    }
}
