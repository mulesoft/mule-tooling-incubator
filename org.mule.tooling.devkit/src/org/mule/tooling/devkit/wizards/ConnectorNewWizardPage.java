package org.mule.tooling.devkit.wizards;

import java.io.File;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.mule.tooling.ui.common.FileChooserComposite;
import org.mule.tooling.ui.common.FolderChooserComposite;


public class ConnectorNewWizardPage extends ModuleNewWizardPage {

    protected Button fromWsdlCheckBox;
    protected Button oauthEnabled;
    protected Label wsdlLocationLabel;
    protected Label sourceLocationLabel;
	private FileChooserComposite fileChooser;
	private FolderChooserComposite folderChooser;

    
    public ConnectorNewWizardPage(ISelection selection) {
        super(selection);
        setTitle("Cloud Connector Wizard");
        setDescription("This wizard creates a Mule Cloud Connector template");
        this.selection = selection;
        this.nameLabel = "&Connector Name:";
    }
    
    
    @Override
    public void createControl(Composite parent) {
    	Composite container = new Composite(parent, SWT.NULL); 
    	GridLayout layout = new GridLayout();
    	layout.marginHeight = 2;
    	layout.marginBottom = 0;
    	layout.verticalSpacing = 2;
    	layout.marginTop = 0;
		container.setLayout(layout);
        
    	Composite commonContainer = drawCommon(container);
        
    	oauthEnabled = new Button(commonContainer, SWT.CHECK);
    	oauthEnabled.setText("Enable OAuth");
    	oauthEnabled.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
//        Group examplesGroup = new Group(container, SWT.NULL);
//        examplesGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
//        GridLayout exLayout = new GridLayout(2, false);
//        exLayout.marginWidth = 4;
//        examplesGroup.setLayout(exLayout);
//        examplesGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
//        examplesGroup.setText("Templates:");
//
//        fromWsdlCheckBox = new Button(examplesGroup, SWT.CHECK);
//        fromWsdlCheckBox.setText("Create project based on an wsdl file:");
//        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
//        layoutData.horizontalSpan =2;
//		fromWsdlCheckBox.setLayoutData(layoutData);
//        fromWsdlCheckBox.addSelectionListener(new SelectionListener() {
//
//            public void widgetSelected(SelectionEvent e) {
//                boolean enabled = fromWsdlCheckBox.getSelection();
//                fileChooser.setEnabled(enabled);
//                folderChooser.setEnabled(enabled);
//            }
//
//            public void widgetDefaultSelected(SelectionEvent e) {
//            }
//        });
//        
//        wsdlLocationLabel = new Label(examplesGroup,SWT.NULL);
//        wsdlLocationLabel.setText("WSDL file location:");
//        wsdlLocationLabel.setLayoutData(new GridData(GridData.BEGINNING));
//        
//        fileChooser = new FileChooserComposite(examplesGroup, SWT.NULL);
//        fileChooser.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//        
//        sourceLocationLabel = new Label(examplesGroup,SWT.NULL);
//        sourceLocationLabel.setText("Source folder location:");
//        sourceLocationLabel.setLayoutData(new GridData(GridData.BEGINNING));
//        folderChooser = new FolderChooserComposite(examplesGroup, SWT.NULL);
//        folderChooser.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//        
//
//        wsdlLocationLabel.setEnabled(false);
//        fileChooser.setEnabled(false);
//        folderChooser.setEnabled(false);
        
        setControl(container);
    }


	public boolean isOAuthEnabled() {
		return oauthEnabled.getSelection();
	}

	public boolean isFromWsdl()
	{
		return false;
				//fromWsdlCheckBox.getSelection();
	}
	
	public String getWsdlFile()
	{
		return null;
				//fileChooser.getFilePath();
	}


    


}