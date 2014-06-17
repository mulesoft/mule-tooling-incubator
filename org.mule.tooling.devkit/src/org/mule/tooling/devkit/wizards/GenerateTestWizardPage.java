package org.mule.tooling.devkit.wizards;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;
import org.mule.tooling.devkit.ASTUtils;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.common.TestDataModelDto;
import org.mule.tooling.devkit.common.TestDataModelDto.ExportPolicy;
import org.mule.tooling.ui.MuleUiConstants;
import org.mule.tooling.ui.utils.UiUtils;

public class GenerateTestWizardPage extends WizardPage {

    private static final String GENERAL_MESSAGE = "Select the operation and type of generation to be performed";
    private static final String GROUP_TITTLE_FUNCTIONAL_TESTCASES = "Functional Tests";
    private static final String GROUP_TITLE_INTEROP_INPUT = "Studio Interop Tests";

    private static final String CREDENTIALS = "Credentials File:";
    private static final String BROWSE = "Browse";
    private static final String FUNCTIONAL_FILES = "Generate Test Case Scaffolding";
    private static final String INTEROP_FILES = "Generate Interop Test Data Files";

    private static final String TESTDATA_XML = "interop-testdata.xml";
    private static final String LABEL_FUNCTIONAL_NAME = "Generate Functional Test Data Files";
    private static final String LABEL_INTEROP_DATA_FILE = "Test Data File";
    private static final String EXTENSION_FILTER = "*.properties";

    private final TestDataModelDto dataModel;
    private List<String> processors = new ArrayList<String>();
    private IProject project;

    private Text txtOutputFileName;
    private Text txtCredsFile;
    private Boolean selectedScafolding = false;
    private Boolean selectedInterop = false;
    private Boolean selectedFunctional = false;
    private CheckboxTableViewer viewer;
    private Button browserButton;
    
    private Group functionalGroup;
    private Group interopGroup;

    public GenerateTestWizardPage(IProject selectedProject, TestDataModelDto dataModel) {
        super("Generation Options");
        setTitle("Test Scaffolding Generation");
        setDescription(GENERAL_MESSAGE);
        
        this.dataModel = dataModel != null ? new TestDataModelDto(dataModel) : new TestDataModelDto();
        this.project = selectedProject;
        this.processors = getProjectProcessorsNames();
    }

    @Override
    public void createControl(Composite parent) {

        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(1, false);
        container.setLayout(layout);
        
        GridData gdata = new GridData();
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;
        gdata.grabExcessVerticalSpace = true;
        container.setLayoutData(gdata);

        createProcessorsSelector(container);
        createFunctionalTestCasesGroup(container);
        createInteropPropertiesGroup(container);
        initializeFields();
        
        setControl(container);
        GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(2, 2, 10, 0).margins(0, 0).spacing(0, 0).applyTo(container);
        GridDataFactory.fillDefaults().indent(0, 0).applyTo(container);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(container, "org.mule.tooling.devkit.myId"); 
    }

    private void createFunctionalTestCasesGroup(Composite container) {
        functionalGroup = UiUtils.createGroupWithTitle(container, GROUP_TITTLE_FUNCTIONAL_TESTCASES, 1);

        createGenerationTypeCheckboxInput(functionalGroup, FUNCTIONAL_FILES, 1, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedScafolding = ((Button) e.getSource()).getSelection();
            }
        });
        
        createGenerationTypeCheckboxInput(functionalGroup, LABEL_FUNCTIONAL_NAME, 1, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedFunctional = ((Button) e.getSource()).getSelection();
            }
        });
    }

    private void createInteropPropertiesGroup(Composite container) {
        interopGroup = UiUtils.createGroupWithTitle(container, GROUP_TITLE_INTEROP_INPUT, 3);

        createGenerationTypeCheckboxInput(interopGroup, INTEROP_FILES, 3, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedInterop = ((Button) e.getSource()).getSelection();

                txtCredsFile.setEnabled(selectedInterop);
                txtOutputFileName.setEnabled(selectedInterop);
                browserButton.setEnabled(selectedInterop);
            }
        });
        createNameInput(interopGroup);
        createFileInput(interopGroup);
        createBrowser(interopGroup);
    }

    private void initializeFields() {
        setControlsEnable(false, functionalGroup.getChildren());
        setControlsEnable(false, interopGroup.getChildren());
        setPageComplete(false);
    }
    
    private void setControlsEnable(boolean enabled, Control... controls){
        for (Control part : controls){
            part.setEnabled(enabled);
        }
    }

    private void createNameInput(Composite container) {
        ModifyListener interopFileListener = new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
            }
        };

        txtOutputFileName = initializeTextField(container, LABEL_INTEROP_DATA_FILE, TESTDATA_XML, 2, interopFileListener);
        txtOutputFileName.setText(TESTDATA_XML);
    }

    private void createFileInput(Composite container) {
        createLabel(container, CREDENTIALS);

        GridData dataFileName = new GridData();
        dataFileName.grabExcessHorizontalSpace = true;
        dataFileName.horizontalAlignment = SWT.FILL;
        dataFileName.horizontalSpan = 1;

        txtCredsFile = new Text(container, SWT.BORDER);
        txtCredsFile.setLayoutData(dataFileName);

        txtCredsFile.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String text = ((Text) e.widget).getText();

                if (!text.isEmpty() && !new File(text).exists()) {
                    setMessage("Invalid credentials file. File does not exist", IMessageProvider.ERROR);
                    setPageComplete(false);
                } else {
                    setMessage(GENERAL_MESSAGE, IMessageProvider.INFORMATION);
                    setPageComplete(true);
                }
            }
        });
    }

    private void createBrowser(Composite container) {

        Button button = new Button(container, SWT.PUSH);
        GridData dataButton = new GridData();
        dataButton.grabExcessHorizontalSpace = false;
        dataButton.horizontalAlignment = SWT.RIGHT;
        dataButton.horizontalSpan = 1;

        button.setLayoutData(dataButton);
        button.setText(BROWSE);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell());
                fileDialog.setText("Select File");

                fileDialog.setFilterExtensions(new String[] { EXTENSION_FILTER });
                fileDialog.setFilterNames(new String[] { "Properties(*.properties)" });

                String selected = fileDialog.open();
                
                txtCredsFile.setText(selected);
            }
        });

        browserButton = button;
    }

    private void createGenerationTypeCheckboxInput(Composite container, String label, int horizontalSpan, SelectionAdapter adapter) {

        Button checkBox = new Button(container, SWT.CHECK);

        GridData data = new GridData();
        data.grabExcessHorizontalSpace = false;
        data.horizontalAlignment = SWT.LEFT;
        data.horizontalSpan = horizontalSpan;
        checkBox.setLayoutData(data);

        checkBox.addSelectionListener(adapter);
        checkBox.setText(label);
    }

    private Text initializeTextField(Composite container, String labelText, String defaultValue, int span, ModifyListener modifyListener) {

        Label label = new Label(container, SWT.NULL);
        label.setText(labelText);
        label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(MuleUiConstants.LABEL_WIDTH, SWT.DEFAULT).create());

        Text textField = new Text(container, SWT.BORDER);

        GridData dataFileName = new GridData();
        dataFileName.grabExcessHorizontalSpace = true;
        dataFileName.horizontalAlignment = SWT.FILL;
        dataFileName.horizontalSpan = span;

        textField.setLayoutData(dataFileName);
        textField.setText(defaultValue);
        textField.addModifyListener(modifyListener);

        return textField;
    }

    private void createLabel(Composite container, String text) {
        Label lbtCredentials = new Label(container, SWT.NONE);

        GridData label = new GridData();
        label.grabExcessHorizontalSpace = false;
        label.horizontalAlignment = SWT.BEGINNING;
        label.horizontalSpan = 1;

        lbtCredentials.setLayoutData(label);
        lbtCredentials.setText(text);
    }

    
    
    private void createProcessorsSelector(Composite parent) {

        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(4, false);
        layout.verticalSpacing = 2;
        layout.makeColumnsEqualWidth = false;
        container.setLayout(layout);

        GridData gdata = new GridData();
        gdata.horizontalAlignment = GridData.FILL;
        gdata.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
        gdata.grabExcessHorizontalSpace = true;
        gdata.grabExcessVerticalSpace = true;

        container.setLayoutData(gdata);

        createProcessorsTable(container, processors.toArray());

        createTableSelectionButton(container, "Select All", true);
        createTableSelectionButton(container, "Deselect All", false);

        setControl(container);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(container, "org.mule.tooling.devkit.myId2");
    }


    private void createProcessorsTable(Composite container, Object input) {

        // define the TableViewer
        viewer = CheckboxTableViewer.newCheckList(container, SWT.CHECK | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

        // define layout for the viewer
        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalSpan = 3;
        gridData.verticalSpan = 24;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        viewer.getControl().setLayoutData(gridData);

        viewer.setContentProvider(ArrayContentProvider.getInstance());
        viewer.setComparator(new ViewerComparator() {
          public int compare(Viewer viewer, Object e1, Object e2) {
            return ((String) e1).compareTo((String) e2);
          };
        });
        
        viewer.setInput(input);
        viewer.setLabelProvider(new LabelProvider() {

            public String getText(Object element) {
                return (String) element;
            }
        });
        
        viewer.getTable().addListener(SWT.Selection, new Listener() {
        
            public void handleEvent(Event event) {
              if (event.detail == SWT.CHECK) {
                if (viewer.getCheckedElements().length > 0){  
                    enableGlobalFields();

                }else{
                    disableGlobalFields();
                }
              }
            }
        });
    }
    
    private void disableGlobalFields() {
        setControlsEnable(false, functionalGroup.getChildren());
        setControlsEnable(false, interopGroup.getChildren());
        setPageComplete(false);
    }

    private void enableGlobalFields() {
        setControlsEnable(true, functionalGroup.getChildren());
        setControlsEnable(true, interopGroup.getChildren());
        
        setInteropInputEnable(false);
        setPageComplete(true);
    }

    private void setInteropInputEnable(boolean enabled) {
        txtCredsFile.setEnabled(enabled);
        txtOutputFileName.setEnabled(enabled);
        browserButton.setEnabled(enabled);
    }
    
    public void createTableSelectionButton(Composite container, String text, final Boolean setChecked) {

        Button button = new Button(container, SWT.PUSH);
        GridData dataButton = new GridData();
        dataButton.grabExcessHorizontalSpace = false;
        dataButton.horizontalAlignment = SWT.FILL;
        dataButton.verticalAlignment = SWT.BEGINNING;
        dataButton.horizontalSpan = 1;

        button.setLayoutData(dataButton);
        button.setText(text);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                viewer.setAllChecked(setChecked);
                if (viewer.getCheckedElements().length > 0){  
                    enableGlobalFields();

                }else{
                    disableGlobalFields();
                    setMessage(GENERAL_MESSAGE);
                }
            }
        });
    }

    private List<String> getProjectProcessorsNames() {
        
        List<String> names = new LinkedList<String>();
        List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
        
        try {
            methods = DevkitUtils.getProjectProcessors(project);
        
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Connector not found", "Unable to find the connector class, please select one");
            methods = getConnectorFromUserPrompt(methods);
        }
        
        for (MethodDeclaration proc : methods)
            names.add(proc.getName().toString());

        return names;
    }

    public List<MethodDeclaration> getConnectorFromUserPrompt(List<MethodDeclaration> methods) {
        ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(Display.getCurrent().getActiveShell(), project.getFolder(DevkitUtils.MAIN_JAVA_FOLDER), IResource.FILE);
        dialog.setTitle("Please select your connector's class");

        while ((methods == null || methods.isEmpty()) && dialog.open() == 0){
            
            Object[] result = dialog.getResult();
            
            if (result != null){
                IResource connectorResource = ((IResource) result[0]);
                IJavaElement element = (IJavaElement) connectorResource.getAdapter(IJavaElement.class);
                if (element.getElementType() == IJavaElement.COMPILATION_UNIT){ 
                    try {
                        methods = DevkitUtils.getProjectProcessors(ASTUtils.parse((ICompilationUnit) element));
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }    
            }
            
            if (methods == null || methods.isEmpty()){
                MessageDialog.openError(Display.getCurrent().getActiveShell(), "Invalid Connector", "No processors were found in the selected file");
            }
        }
        return methods;
    }
    
    private void saveInput(){

        dataModel.setCredentialsFile(txtCredsFile.getText());
            
        dataModel.setOutputFile(txtOutputFileName.getText());

        dataModel.setSelectedScafolding(selectedScafolding);
        dataModel.setSelectedInterop(selectedInterop);
        dataModel.setSelectedFunctional(selectedFunctional);

        dataModel.setExportInteropPolicy(ExportPolicy.UPDATE);
        dataModel.setExportFunctionalPolicy(ExportPolicy.UPDATE);
        
        StringBuilder sb = new StringBuilder();
        for(Object name: viewer.getCheckedElements()){
            sb.append((String)name);
            sb.append(",");
        }
        
       dataModel.setFilteredProcessors(sb.toString());
    }

    public TestDataModelDto getDataModel() {
        this.saveInput();
        return dataModel;
    }
}
