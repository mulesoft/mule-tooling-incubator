package org.mule.tooling.editor;

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

public class DefaultVisitor implements IElementVisitor {

    @Override
    public void visit(CloudConnectorMessageSource cloudConnectorMessageSource) {

    }

    @Override
    public void visit(Global global) {

    }

    @Override
    public void visit(GlobalCloudConnector globalCloudConnector) {

    }

    @Override
    public void visit(GlobalEndpoint globalEndpoint) {

    }

    @Override
    public void visit(GlobalFilter globalFilter) {

    }

    @Override
    public void visit(GlobalTransformer globalTransformer) {

    }

    @Override
    public void visit(AttributeCategory attributeCategory) {

    }

    @Override
    public void visit(BooleanEditor booleanEditor) {

    }

    @Override
    public void visit(Button button) {

    }

    @Override
    public void visit(Case caseEditorElement) {

    }

    @Override
    public void visit(ChildElement childElement) {

    }

    @Override
    public void visit(ClassNameEditor classNameEditor) {

    }

    @Override
    public void visit(Custom custom) {

    }

    @Override
    public void visit(Dummy dummy) {

    }

    @Override
    public void visit(DynamicEditor dynamicEditor) {

    }

    @Override
    public void visit(EditorRef editorRef) {

    }

    @Override
    public void visit(ElementControllerList elementControllerList) {

    }

    @Override
    public void visit(ElementControllerListNoExpression elementControllerListNoExpression) {

    }

    @Override
    public void visit(ElementControllerListOfMap elementControllerListOfMap) {

    }

    @Override
    public void visit(ElementControllerMap elementControllerMap) {

    }

    @Override
    public void visit(ElementControllerMapNoExpression elementControllerMapNoExpression) {

    }

    @Override
    public void visit(ElementQuery elementQuery) {

    }

    @Override
    public void visit(EncodingEditor encodingEditor) {

    }

    @Override
    public void visit(EnumEditor enumEditor) {

    }

    @Override
    public void visit(FileEditor fileEditor) {

    }

    @Override
    public void visit(FixedAttribute fixedAttribute) {

    }

    @Override
    public void visit(Group group) {

    }

    @Override
    public void visit(Horizontal horizontal) {

    }

    @Override
    public void visit(IntegerEditor integerEditor) {

    }

    @Override
    public void visit(LabelElement labelElement) {

    }

    @Override
    public void visit(ListEditor listEditor) {

    }

    @Override
    public void visit(LongEditor longEditor) {

    }

    @Override
    public void visit(Mode mode) {

    }

    @Override
    public void visit(ModeSwitch modeSwitch) {

    }

    @Override
    public void visit(NameEditor nameEditor) {

    }

    @Override
    public void visit(NativeLibrary nativeLibrary) {

    }

    @Override
    public void visit(NoOperation noOperation) {

    }

    @Override
    public void visit(Option option) {

    }

    @Override
    public void visit(PasswordEditor passwordEditor) {

    }

    @Override
    public void visit(PathEditor pathEditor) {

    }

    @Override
    public void visit(RadioBoolean radioBoolean) {

    }

    @Override
    public void visit(Regexp regexp) {

    }

    @Override
    public void visit(RequiredLibraries requiredLibraries) {

    }

    @Override
    public void visit(ResourceEditor resource) {

    }

    @Override
    public void visit(SoapInterceptor soapInterceptor) {

    }

    @Override
    public void visit(StringEditor stringEditor) {

    }

    @Override
    public void visit(StringMap stringMap) {

    }

    @Override
    public void visit(SwitchCase switchCase) {

    }

    @Override
    public void visit(TextEditor textEditor) {

    }

    @Override
    public void visit(TimeEditor timeEditor) {

    }

    @Override
    public void visit(TypeChooser typeChooser) {

    }

    @Override
    public void visit(UrlEditor urlEditor) {

    }

    @Override
    public void visit(UseMetaData useMetaData) {

    }

    @Override
    public void visit(Jar jar) {

    }

    @Override
    public void visit(LibrarySet librarySet) {

    }

    @Override
    public void visit(ContainerRef containerRef) {

    }

    @Override
    public void visit(FlowRef flowRef) {

    }

    @Override
    public void visit(GlobalRef globalRef) {

    }

    @Override
    public void visit(ReverseGlobalRef reverseGlobalRef) {

    }

    @Override
    public void visit(Alternative alternative) {

    }

    @Override
    public void visit(CloudConnector cloudConnector) {

    }

    @Override
    public void visit(Component component) {

    }

    @Override
    public void visit(Connector connector) {

    }

    @Override
    public void visit(Container container) {

    }

    @Override
    public void visit(Endpoint endpoint) {

    }

    @Override
    public void visit(Filter filter) {

    }

    @Override
    public void visit(Flow flow) {

    }

    @Override
    public void visit(GraphicalContainer graphicalContainer) {

    }

    @Override
    public void visit(Keyword keyword) {

    }

    @Override
    public void visit(KeywordSet keywordSet) {

    }

    @Override
    public void visit(LocalRef localRef) {

    }

    @Override
    public void visit(MultiSource multiSource) {

    }

    @Override
    public void visit(Namespace namespace) {

    }

    @Override
    public void visit(Nested nested) {

    }

    @Override
    public void visit(NestedContainer nestedContainer) {

    }

    @Override
    public void visit(Pattern pattern) {

    }

    @Override
    public void visit(Radio radio) {

    }

    @Override
    public void visit(RequiredSetAlternatives requiredSetAlternatives) {

    }

    @Override
    public void visit(Transformer transformer) {

    }

    @Override
    public void visit(Wizard wizard) {

    }

    @Override
    public void visit(DateTimeEditor datetimeEditor) {
        
    }
}
