package org.mule.tooling.studio.ui.editor;

import java.util.ArrayList;
import java.util.List;

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
import org.mule.tooling.editor.model.IElementVisitor;
import org.mule.tooling.editor.model.Keyword;
import org.mule.tooling.editor.model.KeywordSet;
import org.mule.tooling.editor.model.LocalRef;
import org.mule.tooling.editor.model.MultiSource;
import org.mule.tooling.editor.model.Namespace;
import org.mule.tooling.editor.model.Nested;
import org.mule.tooling.editor.model.NestedContainer;
import org.mule.tooling.editor.model.AbstractPaletteComponent;
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

public class GetChildVisitor implements IElementVisitor {

    private List<Object> childs;

    public GetChildVisitor() {
        childs = new ArrayList<>();
    }

    private void doGetChildren(AbstractPaletteComponent paletteComponent) {
        if (paletteComponent.getKeywords() != null) {
            childs.add(paletteComponent.getKeywords());
        }
        doGetChildren((EditorElement) paletteComponent);
        if (paletteComponent.getRequiredSetAlternatives() != null) {
            childs.add(paletteComponent.getRequiredSetAlternatives());
        }
    }

    private void doGetChildren(EditorElement editorElement) {
        childs.addAll(editorElement.getAttributeCategories());
    }

    @Override
    public void visit(CloudConnectorMessageSource cloudConnectorMessageSource) {
        doGetChildren(cloudConnectorMessageSource);
    }

    @Override
    public void visit(Global global) {
        doGetChildren(global);
    }

    @Override
    public void visit(GlobalCloudConnector globalCloudConnector) {
        doGetChildren(globalCloudConnector);
        if (globalCloudConnector.getRequired() != null) {
            childs.add(globalCloudConnector.getRequired());
        }
    }

    @Override
    public void visit(GlobalEndpoint globalEndpoint) {
        doGetChildren(globalEndpoint);
    }

    @Override
    public void visit(GlobalFilter globalFilter) {
        doGetChildren(globalFilter);

    }

    @Override
    public void visit(GlobalTransformer globalTransformer) {
        doGetChildren(globalTransformer);
    }

    @Override
    public void visit(AttributeCategory attributeCategory) {
        childs.addAll(attributeCategory.getChilds());
    }

    @Override
    public void visit(BooleanEditor booleanEditor) {

    }

    @Override
    public void visit(Button button) {

    }

    @Override
    public void visit(Case caseEditorElement) {
        childs.addAll(caseEditorElement.getChildElements());

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
        childs.addAll(dynamicEditor.getEditorReferences());
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
        childs.addAll(enumEditor.getOptions());
    }

    @Override
    public void visit(FixedAttribute fixedAttribute) {

    }

    @Override
    public void visit(Group group) {
        childs.addAll(group.getChilds());
        if (group.getUserMetaData() != null) {
            childs.add(group.getUserMetaData());
        }
    }

    @Override
    public void visit(Horizontal horizontal) {
        childs.addAll(horizontal.getChilds());
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
        childs.addAll(modeSwitch.getModes());
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
        childs.addAll(requiredLibraries.getLibraries());
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
        childs.addAll(switchCase.getCases());
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
        childs.addAll(librarySet.getLibraries());
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
        doGetChildren(cloudConnector);
    }

    @Override
    public void visit(Component component) {
        doGetChildren(component);

    }

    @Override
    public void visit(Connector connector) {
        doGetChildren(connector);
    }

    @Override
    public void visit(Container container) {
        doGetChildren(container);
    }

    @Override
    public void visit(Endpoint endpoint) {
        doGetChildren(endpoint);

    }

    @Override
    public void visit(Filter filter) {
        doGetChildren(filter);
    }

    @Override
    public void visit(Flow flow) {
        doGetChildren(flow);
    }

    @Override
    public void visit(GraphicalContainer graphicalContainer) {
        doGetChildren(graphicalContainer);
    }

    @Override
    public void visit(Keyword keyword) {

    }

    @Override
    public void visit(KeywordSet keywordSet) {
        childs.addAll(keywordSet.getKeywords());
    }

    @Override
    public void visit(LocalRef localRef) {

    }

    @Override
    public void visit(MultiSource multiSource) {
        doGetChildren(multiSource);
    }

    @Override
    public void visit(Namespace namespace) {
        childs.addAll(namespace.getComponents());
    }

    @Override
    public void visit(Nested nested) {
        if (!nested.getAttributeCategories().isEmpty()) {
            childs.addAll(nested.getAttributeCategories());
        }
        if (nested.getChildElements() != null) {
            childs.addAll(nested.getChildElements());
        }
    }

    @Override
    public void visit(NestedContainer nestedContainer) {

    }

    @Override
    public void visit(Pattern pattern) {
        doGetChildren(pattern);

    }

    @Override
    public void visit(Radio radio) {
        childs.addAll(radio.getOptions());
    }

    @Override
    public void visit(RequiredSetAlternatives requiredSetAlternatives) {
        childs.addAll(requiredSetAlternatives.getAlternatives());
    }

    @Override
    public void visit(Transformer transformer) {
        doGetChildren(transformer);

    }

    @Override
    public void visit(Wizard wizard) {
        doGetChildren(wizard);
    }

    public List<Object> getChilds() {
        return childs;
    }

    public void setChilds(List<Object> childs) {
        this.childs = childs;
    }

    @Override
    public void visit(FileEditor fileEditor) {

    }

    @Override
    public void visit(DateTimeEditor datetimeEditor) {
        // TODO Auto-generated method stub
        
    }

}
