package org.mule.tooling.ui.contribution.sap.widgets.meta;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.mule.tooling.messageflow.util.MessageFlowUtils;
import org.mule.tooling.metadata.utils.MetadataUtils;
import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.util.PropertiesUtils;
import org.mule.tooling.ui.modules.core.widgets.AttributesPropertyPage;



public abstract class BaseSapDialog extends TitleAreaDialog {

    private AttributesPropertyPage page = null;
    
    public BaseSapDialog(Shell parentShell, AttributesPropertyPage page) {
        super(parentShell);
        setPage(page);
        setHelpAvailable(false);
    }

    protected String getPropertyValue(String name) {
        if (this.page != null) {
            final MessageFlowNode node = this.page.getNode();
            final MessageFlowNode nodeToBeTested = MessageFlowUtils.generateNodeToTest(node, this.page.getHost());
            return PropertiesUtils.getPropertyValue(nodeToBeTested, name);
        } else {
            return null;
        }
    }

    protected String getSapObjectName() {
        return getPropertyValue("functionName");
    }
    
    protected String getSapTypeDescription() {
        if (isIDocType()) {
            return "IDoc";
        } else if (isFunctionType()) {
            return "Function";
        } else {
            return "Unknown";
        }
    }

    protected boolean isIDocType() {
        String value = getPropertyValue("type");
        if (value != null) {
            return value.toLowerCase().contains("idoc");
        } else {
            return false;
        }
    }

    protected boolean isFunctionType() {
        return !isIDocType();
    }    
    
    public AttributesPropertyPage getPage() {
        return page;
    }

    
    public void setPage(AttributesPropertyPage page) {
        this.page = page;
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        // create the top level composite for the dialog area
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        composite.setFont(parent.getFont());
        return composite;
    }
    
    protected void exportToFile(Shell shell, String title, String baseDirectory, String sapObjectName, String extension, String contents) {
        FileDialog dialog = new FileDialog(shell, SWT.SAVE);
        if (baseDirectory != null) {
            dialog.setFilterPath(baseDirectory);
        }
        title = title != null ? title : "Export " + sapObjectName;
        dialog.setFilterExtensions(new String[] {extension});
        dialog.setFileName(escapeObjectName(sapObjectName) + "." + extension);
        dialog.setText(title);
        dialog.setOverwrite(true);

        String path = null;
        if ((path = dialog.open()) != null) {
            try {
                writeFile(path, contents);
                // Refresh
                if(path.startsWith(baseDirectory)) {
                    page.getMuleProject().getJavaProject().getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
                }
            } catch(Exception ex) {
                MetadataUtils.openError(shell, "Failed to save file " + path + ": " + ex.getMessage() , null, ex, title, false);
            }
        }        
    }

    protected String escapeObjectName(String sapObjectName) {
        return StringUtils.replace(sapObjectName, "/", "_");
    }

    protected void writeFile(String path, String contents) throws IOException {
        OutputStream output = null;
        try {
            output = new FileOutputStream(path);
            IOUtils.write(contents, output);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }    
    
}
