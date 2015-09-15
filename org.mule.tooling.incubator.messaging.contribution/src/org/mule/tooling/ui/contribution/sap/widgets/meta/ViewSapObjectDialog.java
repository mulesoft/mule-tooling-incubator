package org.mule.tooling.ui.contribution.sap.widgets.meta;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.mule.tooling.ui.modules.core.widgets.AttributesPropertyPage;
import org.mule.tooling.ui.widgets.util.WidgetUtils;

public class ViewSapObjectDialog extends BaseSapDialog {
    
    private String xmlTemplate = null;
    private String xsd = null;
    private String projectBaseDirectory = null;
    
    private Composite composite = null;
    
    public ViewSapObjectDialog(Shell parentShell, AttributesPropertyPage page) {
        super(parentShell, page);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        setTitle("Details for " + getSapObjectName() + " " + getSapTypeDescription());
        setMessage("View and download XML template and/or XSD for " + getSapObjectName() + " " + getSapTypeDescription());

        composite = (Composite) super.createDialogArea(parent);
        GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).applyTo(composite);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);

        final TabFolder tabFolder = new TabFolder(parent, SWT.TOP);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
        tabFolder.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        tabFolder.setBackgroundMode(SWT.INHERIT_FORCE);

        final TabItem xmlTemplateTabItem = new TabItem(tabFolder, SWT.NULL);
        xmlTemplateTabItem.setText("XML Template");
        Composite xmlComposite = new Composite(tabFolder, SWT.NULL);
        GridLayoutFactory.swtDefaults().numColumns(1).equalWidth(false).applyTo(xmlComposite);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(xmlComposite);
        WidgetUtils.createXmlStyledText(xmlComposite, getXmlTemplate());
        xmlTemplateTabItem.setControl(xmlComposite);
        
        Button exportXmlTemplateButton = new Button(xmlComposite, SWT.PUSH);
        exportXmlTemplateButton.setText("Export ...");
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).hint(SWT.DEFAULT, SWT.DEFAULT).grab(false, false).span(1, 1).applyTo(exportXmlTemplateButton);
        exportXmlTemplateButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                exportToFile(getParentShell(), "Export XML Template for " + getSapTypeDescription() + " " + getSapObjectName(), getProjectBaseDirectory(), getSapObjectName(), "xml", getXmlTemplate());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        
        final TabItem xsdTabItem = new TabItem(tabFolder, SWT.NULL);
        xsdTabItem.setText("XSD");
        Composite xsdComposite = new Composite(tabFolder, SWT.NULL);
        GridLayoutFactory.swtDefaults().numColumns(1).equalWidth(false).applyTo(xsdComposite);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(xsdComposite);
        WidgetUtils.createXmlStyledText(xsdComposite, getXsd());
        xsdTabItem.setControl(xsdComposite);
        
        Button exportXsdButton = new Button(xsdComposite, SWT.PUSH);
        exportXsdButton.setText("Export ...");
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).hint(SWT.DEFAULT, SWT.DEFAULT).grab(false, false).span(1, 1).applyTo(exportXsdButton);
        exportXsdButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                exportToFile(getParentShell(), "Export XSD for " + getSapTypeDescription() + " " + getSapObjectName(), getProjectBaseDirectory(), getSapObjectName(), "xsd", getXsd());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        return composite;
    }

    
    public String getXmlTemplate() {
        return xmlTemplate != null ? xmlTemplate : "";
    }

    
    public void setXmlTemplate(String xmlTemplate) {
        this.xmlTemplate = xmlTemplate;
    }

    
    public String getXsd() {
        return xsd != null ? xsd : "";
    }

    
    public void setXsd(String xsd) {
        this.xsd = xsd;
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
    }

    
    public String getProjectBaseDirectory() {
        return projectBaseDirectory;
    }

    
    public void setProjectBaseDirectory(String projectBaseDirectory) {
        this.projectBaseDirectory = projectBaseDirectory;
    }    
}

