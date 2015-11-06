package org.mule.tooling.studio.ui.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
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
import org.mule.tooling.studio.ui.StudioUIEditorPlugin;
import org.mule.tooling.studio.ui.widget.ComboPart;

public class NamespaceDetailsBlock extends MasterDetailsBlock {

    private FormPage page;
    private TreeViewer viewer;

    public NamespaceDetailsBlock(FormPage page) {
        this.page = page;
    }

    private void configureMenu(final TreeViewer viewer) {
        final MenuManager menuMgr = new MenuManager();

        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        menuMgr.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager manager) {
                if (viewer.getSelection().isEmpty()) {
                    return;
                }

                if (viewer.getSelection() instanceof IStructuredSelection) {
                    IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                    AbstractEditorElement object = (AbstractEditorElement) selection.getFirstElement();
                    MenuOptionsProvider menuOptionsProvider = new MenuOptionsProvider(viewer, menuMgr);
                    object.accept(menuOptionsProvider);
                }
            }

        });
        menuMgr.setRemoveAllWhenShown(true);
        viewer.getControl().setMenu(menu);
    }

    protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
        // final ScrolledForm form = managedForm.getForm();
        IEditorInput input = page.getEditor().getEditorInput();
        FormToolkit toolkit = managedForm.getToolkit();
        Section section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
        section.setText(Messages.getString("NamespaceDetailsBlock.sname")); //$NON-NLS-1$
        section.setDescription(Messages.getString("NamespaceDetailsBlock.sdesc")); //$NON-NLS-1$
        section.marginWidth = 10;
        section.marginHeight = 5;

        Composite client = toolkit.createComposite(section, SWT.WRAP);
        Composite headerContainer = toolkit.createComposite(client, SWT.WRAP);

        toolkit.createLabel(headerContainer, "Select editor", SWT.NULL);
        //TODO Remove combo
        @SuppressWarnings("unused")
        final ComboPart comboPart = new ComboPart(headerContainer, toolkit, toolkit.getBorderStyle());
        headerContainer.setVisible(false);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 2;
        layout.marginHeight = 2;
        headerContainer.setLayout(layout);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 20;
        gd.widthHint = 100;
        gd.horizontalSpan = 2;
        headerContainer.setLayoutData(gd);

        layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 2;
        layout.marginHeight = 2;
        client.setLayout(layout);

        Tree t = toolkit.createTree(client, SWT.NULL);
        gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 20;
        gd.widthHint = 100;
        t.setLayoutData(gd);
        toolkit.paintBordersFor(client);
        Button collapseButton = toolkit.createButton(client, Messages.getString("Collapse"), SWT.PUSH); //$NON-NLS-1$
        gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        collapseButton.setLayoutData(gd);

        section.setClient(client);
        final SectionPart spart = new SectionPart(section);
        managedForm.addPart(spart);
        viewer = new TreeViewer(t);
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                managedForm.fireSelectionChanged(spart, event.getSelection());
            }
        });
        viewer.setContentProvider(new NamespaceContentProvider());
        viewer.setLabelProvider(new NamespaceLabelProvider());
        if (input != null) {
            viewer.setInput(new StudioUIFormEditorInput(input));
        } else {
            viewer.setInput(new StudioUIFormEditorInput());
        }
        viewer.expandToLevel(5);
        collapseButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                viewer.collapseAll();
            }
        });

        configureMenu(viewer);
    }

    protected void createToolBarActions(IManagedForm managedForm) {
        final ScrolledForm form = managedForm.getForm();
        Action haction = new Action("hor", Action.AS_RADIO_BUTTON) { //$NON-NLS-1$

            public void run() {
                sashForm.setOrientation(SWT.HORIZONTAL);
                form.reflow(true);
            }
        };
        haction.setChecked(true);
        haction.setToolTipText(Messages.getString("NamespaceDetailsBlock.horizontal")); //$NON-NLS-1$
        haction.setImageDescriptor(StudioUIEditorPlugin.getDefault().getImageRegistry().getDescriptor(StudioUIEditorPlugin.IMG_HORIZONTAL));
        Action vaction = new Action("ver", Action.AS_RADIO_BUTTON) { //$NON-NLS-1$

            public void run() {
                sashForm.setOrientation(SWT.VERTICAL);
                form.reflow(true);
            }
        };
        vaction.setChecked(false);
        vaction.setToolTipText(Messages.getString("NamespaceDetailsBlock.vertical")); //$NON-NLS-1$
        vaction.setImageDescriptor(StudioUIEditorPlugin.getDefault().getImageRegistry().getDescriptor(StudioUIEditorPlugin.IMG_VERTICAL));
        form.getToolBarManager().add(haction);
        form.getToolBarManager().add(vaction);
    }

    protected void registerPages(DetailsPart detailsPart) {
        detailsPart.registerPage(Alternative.class, new GenericDetailsPage<Alternative>(Alternative.class));
        detailsPart.registerPage(AttributeCategory.class, new GenericDetailsPage<AttributeCategory>(AttributeCategory.class));

        detailsPart.registerPage(BooleanEditor.class, new GenericDetailsPage<BooleanEditor>(BooleanEditor.class));
        detailsPart.registerPage(org.mule.tooling.editor.model.element.Button.class, new GenericDetailsPage<org.mule.tooling.editor.model.element.Button>(
                org.mule.tooling.editor.model.element.Button.class));

        detailsPart.registerPage(Case.class, new GenericDetailsPage<Case>(Case.class));
        detailsPart.registerPage(ChildElement.class, new GenericDetailsPage<ChildElement>(ChildElement.class));
        detailsPart.registerPage(ClassNameEditor.class, new GenericDetailsPage<ClassNameEditor>(ClassNameEditor.class));
        detailsPart.registerPage(CloudConnector.class, new GenericDetailsPage<CloudConnector>(CloudConnector.class));
        detailsPart.registerPage(CloudConnectorMessageSource.class, new GenericDetailsPage<CloudConnectorMessageSource>(CloudConnectorMessageSource.class));
        detailsPart.registerPage(Component.class, new GenericDetailsPage<Component>(Component.class));
        detailsPart.registerPage(Connector.class, new GenericDetailsPage<Connector>(Connector.class));
        detailsPart.registerPage(Container.class, new GenericDetailsPage<Container>(Connector.class));
        detailsPart.registerPage(ContainerRef.class, new GenericDetailsPage<ContainerRef>(ContainerRef.class));
        detailsPart.registerPage(Custom.class, new GenericDetailsPage<Custom>(Custom.class));

        detailsPart.registerPage(Dummy.class, new GenericDetailsPage<Dummy>(Dummy.class));
        detailsPart.registerPage(DynamicEditor.class, new GenericDetailsPage<DynamicEditor>(DynamicEditor.class));

        detailsPart.registerPage(EditorRef.class, new GenericDetailsPage<EditorRef>(EditorRef.class));
        detailsPart.registerPage(ElementControllerList.class, new GenericDetailsPage<ElementControllerList>(ElementControllerList.class));
        detailsPart.registerPage(ElementControllerListNoExpression.class, new GenericDetailsPage<ElementControllerListNoExpression>(ElementControllerListNoExpression.class));
        detailsPart.registerPage(ElementControllerListOfMap.class, new GenericDetailsPage<ElementControllerListOfMap>(ElementControllerListOfMap.class));
        detailsPart.registerPage(ElementControllerMap.class, new GenericDetailsPage<ElementControllerMap>(ElementControllerMap.class));
        detailsPart.registerPage(ElementControllerMapNoExpression.class, new GenericDetailsPage<ElementControllerMapNoExpression>(ElementControllerMapNoExpression.class));
        detailsPart.registerPage(ElementQuery.class, new GenericDetailsPage<ElementQuery>(ElementQuery.class));
        detailsPart.registerPage(EncodingEditor.class, new GenericDetailsPage<EncodingEditor>(EncodingEditor.class));
        detailsPart.registerPage(Endpoint.class, new GenericDetailsPage<Endpoint>(Endpoint.class));
        detailsPart.registerPage(EnumEditor.class, new GenericDetailsPage<EnumEditor>(EnumEditor.class));

        detailsPart.registerPage(FileEditor.class, new GenericDetailsPage<FileEditor>(FileEditor.class));
        detailsPart.registerPage(Filter.class, new GenericDetailsPage<Filter>(Filter.class));
        detailsPart.registerPage(FixedAttribute.class, new GenericDetailsPage<FixedAttribute>(FixedAttribute.class));
        detailsPart.registerPage(Flow.class, new GenericDetailsPage<Flow>(Flow.class));
        detailsPart.registerPage(FlowRef.class, new GenericDetailsPage<FlowRef>(FlowRef.class));

        detailsPart.registerPage(Global.class, new GenericDetailsPage<Global>(Global.class));
        detailsPart.registerPage(GlobalCloudConnector.class, new GenericDetailsPage<GlobalCloudConnector>(GlobalCloudConnector.class));
        detailsPart.registerPage(GlobalEndpoint.class, new GenericDetailsPage<GlobalEndpoint>(GlobalEndpoint.class));
        detailsPart.registerPage(GlobalFilter.class, new GenericDetailsPage<GlobalFilter>(GlobalFilter.class));
        detailsPart.registerPage(GlobalTransformer.class, new GenericDetailsPage<GlobalTransformer>(GlobalTransformer.class));
        detailsPart.registerPage(GlobalRef.class, new GenericDetailsPage<GlobalRef>(GlobalRef.class));
        detailsPart.registerPage(GraphicalContainer.class, new GenericDetailsPage<GraphicalContainer>(GraphicalContainer.class));
        detailsPart.registerPage(Group.class, new GenericDetailsPage<Group>(Group.class));

        detailsPart.registerPage(Horizontal.class, new GenericDetailsPage<Horizontal>(Horizontal.class));

        detailsPart.registerPage(IntegerEditor.class, new GenericDetailsPage<IntegerEditor>(IntegerEditor.class));

        detailsPart.registerPage(Jar.class, new GenericDetailsPage<Jar>(Jar.class));

        detailsPart.registerPage(Keyword.class, new GenericDetailsPage<Keyword>(Keyword.class));
        detailsPart.registerPage(KeywordSet.class, new GenericDetailsPage<KeywordSet>(KeywordSet.class));

        detailsPart.registerPage(LabelElement.class, new GenericDetailsPage<LabelElement>(LabelElement.class));
        detailsPart.registerPage(ListEditor.class, new GenericDetailsPage<ListEditor>(ListEditor.class));
        detailsPart.registerPage(LocalRef.class, new GenericDetailsPage<LocalRef>(LocalRef.class));
        detailsPart.registerPage(LongEditor.class, new GenericDetailsPage<LongEditor>(LongEditor.class));

        detailsPart.registerPage(Mode.class, new GenericDetailsPage<Mode>(Mode.class));
        detailsPart.registerPage(ModeSwitch.class, new GenericDetailsPage<ModeSwitch>(ModeSwitch.class));
        detailsPart.registerPage(MultiSource.class, new GenericDetailsPage<MultiSource>(MultiSource.class));

        detailsPart.registerPage(NameEditor.class, new GenericDetailsPage<NameEditor>(NameEditor.class));
        detailsPart.registerPage(Namespace.class, new GenericDetailsPage<Namespace>(Namespace.class));
        detailsPart.registerPage(NativeLibrary.class, new GenericDetailsPage<NativeLibrary>(NativeLibrary.class));
        detailsPart.registerPage(Nested.class, new GenericDetailsPage<Nested>(Nested.class));
        detailsPart.registerPage(NestedContainer.class, new GenericDetailsPage<NestedContainer>(NestedContainer.class));
        detailsPart.registerPage(NoOperation.class, new GenericDetailsPage<NoOperation>(NoOperation.class));

        detailsPart.registerPage(Option.class, new GenericDetailsPage<Option>(Option.class));

        detailsPart.registerPage(PasswordEditor.class, new GenericDetailsPage<PasswordEditor>(PasswordEditor.class));
        detailsPart.registerPage(PathEditor.class, new GenericDetailsPage<PathEditor>(PathEditor.class));
        detailsPart.registerPage(Pattern.class, new GenericDetailsPage<Pattern>(Pattern.class));

        detailsPart.registerPage(RadioBoolean.class, new GenericDetailsPage<RadioBoolean>(RadioBoolean.class));
        detailsPart.registerPage(Radio.class, new GenericDetailsPage<Radio>(Radio.class));
        detailsPart.registerPage(Regexp.class, new GenericDetailsPage<Regexp>(Regexp.class));
        detailsPart.registerPage(RequiredSetAlternatives.class, new GenericDetailsPage<RequiredSetAlternatives>(RequiredSetAlternatives.class));
        detailsPart.registerPage(ResourceEditor.class, new GenericDetailsPage<ResourceEditor>(ResourceEditor.class));
        detailsPart.registerPage(ReverseGlobalRef.class, new GenericDetailsPage<ReverseGlobalRef>(ReverseGlobalRef.class));

        detailsPart.registerPage(SoapInterceptor.class, new GenericDetailsPage<SoapInterceptor>(SoapInterceptor.class));
        detailsPart.registerPage(StringEditor.class, new GenericDetailsPage<StringEditor>(StringEditor.class));
        detailsPart.registerPage(StringMap.class, new GenericDetailsPage<StringMap>(StringMap.class));
        detailsPart.registerPage(SwitchCase.class, new GenericDetailsPage<SwitchCase>(SwitchCase.class));

        detailsPart.registerPage(TextEditor.class, new GenericDetailsPage<TextEditor>(TextEditor.class));
        detailsPart.registerPage(TimeEditor.class, new GenericDetailsPage<TimeEditor>(TimeEditor.class));
        detailsPart.registerPage(Transformer.class, new GenericDetailsPage<Transformer>(Transformer.class));
        detailsPart.registerPage(TypeChooser.class, new GenericDetailsPage<TypeChooser>(TypeChooser.class));

        detailsPart.registerPage(UrlEditor.class, new GenericDetailsPage<UrlEditor>(UrlEditor.class));
        detailsPart.registerPage(UseMetaData.class, new GenericDetailsPage<UseMetaData>(UseMetaData.class));

        detailsPart.registerPage(Wizard.class, new GenericDetailsPage<Wizard>(Wizard.class));
    }

    public Namespace getModel() {
        return ((StudioUIFormEditorInput) viewer.getInput()).getNamespace();
    }

}