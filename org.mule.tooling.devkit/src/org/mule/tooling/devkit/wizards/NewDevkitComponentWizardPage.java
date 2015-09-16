package org.mule.tooling.devkit.wizards;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.mule.tooling.devkit.ASTUtils;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.component.ComponentBuilderFactory;
import org.mule.tooling.devkit.component.IComponentBuilder;
import org.mule.tooling.devkit.treeview.ModuleVisitor;
import org.mule.tooling.devkit.treeview.model.ProjectRoot;

public class NewDevkitComponentWizardPage extends NewTypeWizardPage {

    private Combo textField;
    private Map<String, String> components;

    IComponentBuilder componentBuilder;

    public NewDevkitComponentWizardPage(String pageName) {
        super(true, "New Anypoint Connector Component");
        this.setTitle("Anypoint Connector Component");
        this.setDescription("Create a New Anypoint Connector Component.");

    }

    private void doStatusUpdate() {
        // define the components for which a status is desired
        IStatus[] status = null;
        if (textField != null && this.getTypeName().equals(this.textField.getText())) {
            status = new IStatus[] { fContainerStatus, isEnclosingTypeSelected() ? fEnclosingTypeStatus : fPackageStatus, fTypeNameStatus, error };
            setPageComplete(false);
        } else {
            status = new IStatus[] { fContainerStatus, isEnclosingTypeSelected() ? fEnclosingTypeStatus : fPackageStatus, fTypeNameStatus, };
        }
        updateStatus(status);
    }

    protected void handleFieldChanged(String fieldName) {
        super.handleFieldChanged(fieldName);
        doStatusUpdate();
    }

    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        loadComponets();
        Composite composite = new Composite(parent, SWT.NONE);
        int nColumns = 4;
        GridLayout layout = new GridLayout();
        layout.numColumns = nColumns;
        composite.setLayout(layout);

        // Create the standard input fields
        createContainerControls(composite, nColumns);
        createPackageControls(composite, nColumns);
        createSeparator(composite, nColumns);
        createComponentControls(composite);
        createTypeNameControls(composite, nColumns);
        createSuperClassControls(composite, nColumns);
        createSeparator(composite, nColumns);
        createSuperInterfacesControls(composite, nColumns);
        createSeparator(composite, nColumns);

        setControl(composite);

        // Initialize the super type field and mark it as read-only
        setSuperClass("", true);
    }

    private void loadComponets() {
        components = new HashMap<String, String>();
        components.put("Configuration", "A Connection Less Strategy.");
        components.put("ConnectionManagement", "A Basic Auth Connection Strategy.");
        components.put("OAuth2", "An OAuth 2.0 Connection Strategy.");
        components.put("MetaDataCategory", "A grouping DataSense class, which will be responsible for returning types  and the descriptions for those types");
        components.put("Handler", "An Exception Handler Component");
        components.put("Connector", "A class that will export its functionality as a Mule Connector");
        components.put("ProviderAwarePagingDelegate", "A ProviderAwarePagingDelegate is a component capable of consuming a data feed in pages.");
    }

    private void createComponentControls(Composite composite) {
        Label label = new Label(composite, SWT.NONE);
        label.setText("Component Type:");
        GridDataFactory.fillDefaults().grab(false, false).applyTo(label);
        // Create the checkbox controlling whether we want stubs
        textField = new Combo(composite, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
        textField.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).grab(true, false).align(SWT.FILL, SWT.FILL).create());
        textField.setItems(components.keySet().toArray(new String[components.size()]));
        textField.setText("Configuration");

        final ControlDecoration deco = new ControlDecoration(textField, SWT.TOP | SWT.LEFT);
        Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage();
        textField.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                deco.setDescriptionText(getCommentForCombo());
                updateComponent();

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            	deco.setDescriptionText(getCommentForCombo());
                updateComponent();
            }
        });
        deco.setDescriptionText(getCommentForCombo());
        deco.setImage(image);
        deco.setShowOnlyOnFocus(false);
        Composite emptyComposite = new Composite(composite, SWT.NONE);
        GridDataFactory.swtDefaults().hint(0, 0).applyTo(emptyComposite);
        updateComponent();
    }

    protected String constructCUContent(ICompilationUnit cu, String typeContent, String lineDelimiter) throws CoreException {
        String content = super.constructCUContent(cu, typeContent, lineDelimiter);
        int publicLoc = content.indexOf("public");
        return content.substring(0, publicLoc) + componentBuilder.getAnnotation(cu) + lineDelimiter + content.substring(publicLoc);
    }

    protected void createTypeMembers(IType newType, final ImportsManager imports, IProgressMonitor monitor) throws CoreException {
        super.createTypeMembers(newType, imports, monitor);
        componentBuilder.createTypeMembers(null, newType, imports, monitor);
        createInheritedMethods(newType, false, true, imports, monitor);
    }

    /**
     * The wizard managing this wizard page must call this method during initialization with a corresponding selection.
     */
    public void init(IStructuredSelection selection) {
        IJavaElement jelem = getInitialJavaElement(selection);
        initContainerPage(jelem);
        initTypePage(jelem);
        doStatusUpdate();
    }

    public boolean performFinish() {
        try {
            createType(new NullProgressMonitor());

            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    try {
                        IFile connectorFile = ResourcesPlugin.getWorkspace().getRoot().getFile(getCreatedType().getPath());
                        if (connectorFile.exists()) {
                            IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), connectorFile);
                        } else {
                            System.out.println("WTF");
                        }
                    } catch (CoreException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (CoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void updateComponent() {
        componentBuilder = ComponentBuilderFactory.getBuilder(textField.getText());
        if ("ProviderAwarePagingDelegate".equals(textField.getText())) {
            String providerClass = "Object";
            IPackageFragment[] packages;
            try {
                packages = this.getJavaProject().getPackageFragments();

                boolean hasConnector = false;
                ModuleVisitor visitor = new ModuleVisitor();
                for (IPackageFragment mypackage : packages) {
                    if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE && mypackage.exists()) {
                        if (!mypackage.getPath().toString().contains(DevkitUtils.MAIN_JAVA_FOLDER)) {
                            continue;
                        }
                        hasConnector |= ASTUtils.hasConnector(mypackage, visitor);
                        if (hasConnector) {
                            break;
                        }
                    }
                }
                if (hasConnector) {
                    final ProjectRoot root = visitor.getRoot();
                    providerClass = root.getModules().get(0).getJavaElement().getElementName() + "." + root.getModules().get(0).getName();
                }
            } catch (JavaModelException e) {
                // Ignore error just use Object
            }
            this.setSuperClass("org.mule.streaming.ProviderAwarePagingDelegate<Object, " + providerClass + ">", false);
        } else {
            this.setSuperClass("", true);
        }
    }

    private String getCommentForCombo() {
        return this.components.get(this.textField.getText());
    }

    IStatus error = new IStatus() {

        @Override
        public IStatus[] getChildren() {
            return null;
        }

        @Override
        public int getCode() {
            return IStatus.ERROR;
        }

        @Override
        public Throwable getException() {
            return null;
        }

        @Override
        public String getMessage() {
            return "The type name specified cannot be used for the selected Component";
        }

        @Override
        public String getPlugin() {
            return null;
        }

        @Override
        public int getSeverity() {
            return IStatus.ERROR;
        }

        @Override
        public boolean isMultiStatus() {
            return false;
        }

        @Override
        public boolean isOK() {
            return false;
        }

        @Override
        public boolean matches(int severityMask) {
            return IStatus.ERROR == severityMask;
        }
    };
}
