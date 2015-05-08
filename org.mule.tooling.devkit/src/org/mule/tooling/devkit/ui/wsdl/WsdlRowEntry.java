package org.mule.tooling.devkit.ui.wsdl;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.mule.tooling.devkit.ui.LabelledText;

public class WsdlRowEntry implements ModifyListener {

    /** wrappers to draw parameter that will be added to the parameterList */
    private Composite wrapper;
    private Composite rowWrapper;
    final Composite parent;
    LabelledText locationText;
    LabelledText displayText;
    
    public WsdlRowEntry(Composite parent) {
        this.parent = parent;
    }

    public void createControl() {
        rowWrapper = new Composite(parent, SWT.NONE);
        wrapper = new Composite(rowWrapper, SWT.NONE);
        
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(rowWrapper);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(rowWrapper);
        
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).applyTo(wrapper);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(wrapper);

        locationText = new LabelledText(wrapper, "Location");
        displayText = new LabelledText(wrapper, "Display Name");
        locationText.addModifyListener(this);
        displayText.addModifyListener(this);
    }

    public void validate() {
        if (StringUtils.isEmpty(locationText.getText())) {
            locationText.showError("Location is required");
        } else {
            locationText.hideError();
        }
        if (StringUtils.isEmpty(displayText.getText())) {
            displayText.showError("Description is required");
        } else {
            displayText.hideError();
        }
    }

    public Composite getControl() {
        return rowWrapper;
    }

    public void dispose() {
        rowWrapper.dispose();
    }

    @Override
    public void modifyText(ModifyEvent e) {
        validate();
    }

    public void setLocation(String result) {
        locationText.setText(result);
    }

    public String getLocation() {
        return locationText.getText();
    }

    public String getDisplayName() {
        return displayText.getText();
    }

    public void setDisplayName(String displayName) {
        displayText.setText(displayName);
    }
}
