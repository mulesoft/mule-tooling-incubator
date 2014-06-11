package org.mule.tooling.devkit.wizards;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.common.TestDataModelDto;

public class GenerateTestWizardPageAdvance extends WizardPage {

    private static final String pageName = "";

    private TestDataModelDto dataModel;
    
    private IProject selectedProject;
    private CheckboxTableViewer viewer;
    private List<String> processors = new ArrayList<String>();
    
    protected GenerateTestWizardPageAdvance(IProject selectedProject, TestDataModelDto dataModel) {
       super(pageName);

        this.dataModel = dataModel;
        this.selectedProject = selectedProject;
        this.processors = getProjectProcessorsNames();
    }


    private List<String> getProjectProcessorsNames() {
        List<String> names = new LinkedList<String>();
        for(MethodDeclaration proc : DevkitUtils.getProjectProcessors(selectedProject))
            names.add(proc.getName().toString());

        return names;
    }


    @Override
    public void createControl(Composite parent) {

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


    public void createProcessorsTable(Composite container, Object input) {

        // define the TableViewer
        viewer = CheckboxTableViewer.newCheckList(container, SWT.CHECK | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

        // define layout for the viewer
        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalSpan = 3;
        gridData.verticalSpan = 2;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        viewer.getControl().setLayoutData(gridData);

        viewer.setContentProvider(ArrayContentProvider.getInstance());
        viewer.setInput(input);
        viewer.setLabelProvider(new LabelProvider() {

            public String getText(Object element) {
                return (String) element;
            }
        });
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
            }
        });
    }

    public TestDataModelDto getDataModel() {
        saveInput();
        return dataModel;
    }


    private void saveInput() {
        StringBuilder sb = new StringBuilder();
        for(Object name: viewer.getCheckedElements()){
            sb.append((String)name);
            sb.append(",");
        }
        
       dataModel.setFilteredProcessors(sb.toString());
    }
}
