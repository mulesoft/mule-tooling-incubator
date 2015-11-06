package org.mule.tooling.studio.ui.editor;

import java.lang.reflect.Modifier;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;
import org.mule.tooling.editor.model.AbstractPaletteComponent;
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
import org.mule.tooling.editor.model.Pattern;
import org.mule.tooling.editor.model.Radio;
import org.mule.tooling.editor.model.RequiredSetAlternatives;
import org.mule.tooling.editor.model.Transformer;
import org.mule.tooling.editor.model.Wizard;
import org.mule.tooling.editor.model.element.AttributeCategory;
import org.mule.tooling.editor.model.element.BaseChildEditorElement;
import org.mule.tooling.editor.model.element.BaseFieldEditorElement;
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

public class MenuOptionsProvider implements IElementVisitor {

    private final TreeViewer viewer;
    private final IMenuManager manager;

    public MenuOptionsProvider(TreeViewer viewer, IMenuManager manager) {
        super();
        this.viewer = viewer;
        this.manager = manager;
    }

    private void doVisit(final AbstractPaletteComponent palleteComponent) {
        if (palleteComponent.getKeywords() == null) {
            manager.add(new Action() {

                @Override
                public void run() {
                    palleteComponent.setKeywords(new KeywordSet());
                    viewer.refresh();
                }

                @Override
                public String getText() {
                    return "Add KeywordSet";
                }
            });
        }
        if (palleteComponent.getRequiredSetAlternatives() == null) {
            manager.add(new Action() {

                @Override
                public void run() {
                    palleteComponent.setRequiredSetAlternatives(new RequiredSetAlternatives());
                    viewer.refresh();
                }

                @Override
                public String getText() {
                    return "Add RequiredSetAlternatives";
                }
            });
        }
        manager.add(new Action() {

            @Override
            public void run() {
                palleteComponent.getAttributeCategories().add(new AttributeCategory());
                viewer.refresh();
            }

            @Override
            public String getText() {
                return "Add AttributeCategory";
            }
        });
    }

    @Override
    public void visit(final CloudConnectorMessageSource cloudConnectorMessageSource) {
        manager.add(new Action() {

            @Override
            public void run() {
                cloudConnectorMessageSource.getAttributeCategories().add(new AttributeCategory());
                viewer.refresh();
            }

            @Override
            public String getText() {
                return "Add AttributeCategory";
            }
        });
    }

    @Override
    public void visit(final Global global) {
        manager.add(new Action() {

            @Override
            public void run() {
                global.getAttributeCategories().add(new AttributeCategory());
                viewer.refresh();
            }

            @Override
            public String getText() {
                return "Add AttributeCategory";
            }
        });
    }

    @Override
    public void visit(final GlobalCloudConnector globalCloudConnector) {
        if (globalCloudConnector.getRequired() == null) {
            manager.add(new Action() {

                @Override
                public void run() {
                    globalCloudConnector.setRequired(new RequiredLibraries());
                    viewer.refresh();
                }

                @Override
                public String getText() {
                    return "Add Required Libraries";
                }
            });
        }
        manager.add(new Action() {

            @Override
            public void run() {
                globalCloudConnector.getAttributeCategories().add(new AttributeCategory());
                viewer.refresh();
            }

            @Override
            public String getText() {
                return "Add AttributeCategory";
            }
        });
    }

    @Override
    public void visit(final GlobalEndpoint globalEndpoint) {
        manager.add(new Action() {

            @Override
            public void run() {
                globalEndpoint.getAttributeCategories().add(new AttributeCategory());
                viewer.refresh();
            }

            @Override
            public String getText() {
                return "Add AttributeCategory";
            }
        });

    }

    @Override
    public void visit(final GlobalFilter globalFilter) {
        manager.add(new Action() {

            @Override
            public void run() {
                globalFilter.getAttributeCategories().add(new AttributeCategory());
                viewer.refresh();
            }

            @Override
            public String getText() {
                return "Add AttributeCategory";
            }
        });
    }

    @Override
    public void visit(final GlobalTransformer globalTransformer) {
        manager.add(new Action() {

            @Override
            public void run() {
                globalTransformer.getAttributeCategories().add(new AttributeCategory());
                viewer.refresh();
            }

            @Override
            public String getText() {
                return "Add AttributeCategory";
            }
        });
    }

    @Override
    public void visit(AttributeCategory attributeCategory) {
        XmlSeeAlso annotation = BaseChildEditorElement.class.getAnnotation(XmlSeeAlso.class);
        addMenuEntry(manager, annotation, attributeCategory);
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
    public void visit(final DynamicEditor dynamicEditor) {
        manager.add(new Action() {

            @Override
            public void run() {
                dynamicEditor.getEditorReferences().add(new EditorRef());
                viewer.refresh();
            }

            @Override
            public String getText() {
                return "Add EditorRef";
            }
        });

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
    public void visit(final EnumEditor enumEditor) {
        manager.add(new Action() {

            @Override
            public void run() {
                enumEditor.getOptions().add(new Option());
                viewer.refresh();
            }

            @Override
            public String getText() {
                return "Add Option";
            }
        });
    }

    @Override
    public void visit(FixedAttribute fixedAttribute) {

    }

    @Override
    public void visit(Group group) {
        XmlSeeAlso annotation = BaseFieldEditorElement.class.getAnnotation(XmlSeeAlso.class);
        addMenuEntry(manager, annotation, group);
    }

    @Override
    public void visit(Horizontal horizontal) {
        XmlSeeAlso annotation = BaseFieldEditorElement.class.getAnnotation(XmlSeeAlso.class);
        addMenuEntry(manager, annotation, horizontal);
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
    public void visit(final ModeSwitch modeSwitch) {
        if (!modeSwitch.getModes().contains(new NoOperation())) {
            manager.add(new Action() {

                @Override
                public void run() {
                    modeSwitch.getModes().add(new NoOperation());
                    viewer.refresh();
                }

                @Override
                public String getText() {
                    return "Add No-Operation";
                }
            });
        }
        manager.add(new Action() {

            @Override
            public void run() {
                modeSwitch.getModes().add(new Mode());
                viewer.refresh();
            }

            @Override
            public String getText() {
                return "Add Mode";
            }
        });
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
        doVisit(cloudConnector);
    }

    @Override
    public void visit(Component component) {

    }

    @Override
    public void visit(Connector connector) {
        doVisit(connector);
    }

    @Override
    public void visit(Container container) {
        doVisit(container);
    }

    @Override
    public void visit(Endpoint endpoint) {
        doVisit(endpoint);
    }

    @Override
    public void visit(Filter filter) {
        doVisit(filter);
    }

    @Override
    public void visit(Flow flow) {
        doVisit(flow);
    }

    @Override
    public void visit(GraphicalContainer graphicalContainer) {

    }

    @Override
    public void visit(Keyword keyword) {

    }

    @Override
    public void visit(final KeywordSet keywordSet) {
        manager.add(new Action() {

            @Override
            public void run() {
                keywordSet.getKeywords().add(new Keyword());
                viewer.refresh();
            }

            @Override
            public String getText() {
                return "Add Keyword";
            }
        });
        GetParentVisitor parentVisitor = new GetParentVisitor(keywordSet);
        ((StudioUIFormEditorInput) viewer.getInput()).getNamespace().accept(parentVisitor);
        final Object parent = parentVisitor.getParent();
        if (parent != null) {
            manager.add(new Separator());
            manager.add(new Action() {

                @Override
                public void run() {
                    ((AbstractPaletteComponent) parent).setKeywords(null);
                    viewer.refresh();
                }

                @Override
                public String getText() {
                    return "Remove";
                }
            });
        }

    }

    @Override
    public void visit(LocalRef localRef) {

    }

    @Override
    public void visit(MultiSource multiSource) {

    }

    @Override
    public void visit(Namespace namespace) {
        XmlSeeAlso annotation = EditorElement.class.getAnnotation(XmlSeeAlso.class);
        addMenuEntry(manager, annotation, namespace);
    }

    @Override
    public void visit(Nested nested) {
        XmlSeeAlso annotation = BaseChildEditorElement.class.getAnnotation(XmlSeeAlso.class);
        addMenuEntry(manager, annotation, nested);

    }

    @Override
    public void visit(NestedContainer nestedContainer) {

    }

    @Override
    public void visit(Pattern pattern) {
        doVisit(pattern);
    }

    @Override
    public void visit(final Radio radio) {
        manager.add(new Action() {

            @Override
            public void run() {
                radio.getOptions().add(new Option());
                viewer.refresh();
            }

            @Override
            public String getText() {
                return "Add Option";
            }
        });

    }

    @Override
    public void visit(RequiredSetAlternatives requiredSetAlternatives) {

    }

    @Override
    public void visit(Transformer transformer) {
        doVisit(transformer);
    }

    @Override
    public void visit(Wizard wizard) {
        doVisit(wizard);
    }

    @Override
    public void visit(FileEditor fileEditor) {

    }

    private void addMenuEntry(IMenuManager manager, XmlSeeAlso annotation, final Object object) {
        for (final Class<?> extended : annotation.value()) {
            if (Modifier.isAbstract(extended.getModifiers())) {
                XmlSeeAlso innerAnnotation = extended.getAnnotation(XmlSeeAlso.class);
                if (innerAnnotation != null) {
                    MenuManager subMenu = new MenuManager(extended.getSimpleName(), extended.getCanonicalName());
                    manager.add(subMenu);
                    addMenuEntry(subMenu, innerAnnotation, object);
                }
            } else {
                manager.add(new Action() {

                    @Override
                    public void run() {
                        try {
                            if (object instanceof Nested) {
                                ((Nested) object).getChildElements().add((BaseChildEditorElement) extended.newInstance());
                            } else if (object instanceof Namespace) {
                                ((Namespace) object).getComponents().add((EditorElement) extended.newInstance());
                            } else if (object instanceof Group) {
                                ((Group) object).getChilds().add((BaseFieldEditorElement) extended.newInstance());

                            } else if (object instanceof Horizontal) {
                                ((Horizontal) object).getChilds().add((BaseFieldEditorElement) extended.newInstance());
                            } else if (object instanceof AttributeCategory) {
                                ((AttributeCategory) object).getChilds().add((BaseFieldEditorElement) extended.newInstance());
                            }
                            viewer.refresh();
                        } catch (InstantiationException | IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public String getText() {
                        return "Add " + extended.getSimpleName();
                    }

                });
            }
        }
    }

}
