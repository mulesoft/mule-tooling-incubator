package org.mule.tooling.devkit.ui.wsdl;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.mule.tooling.devkit.ui.LabelledText;
import org.mule.tooling.devkit.wizards.ProjectObserver;

public class WsdlRowEntry implements ModifyListener {

    /** wrappers to draw parameter that will be added to the parameterList */
    private Composite wrapper;
    private Composite rowWrapper;
    private final Composite parent;
    private Label locationText;
    private LabelledText displayText;
    private ProjectObserver broadcaster;

    public WsdlRowEntry(Composite parent, ProjectObserver broadcaster) {
        this.parent = parent;
        this.broadcaster = broadcaster;
    }

    public void createControl() {
        rowWrapper = new Composite(parent, SWT.NONE);
        wrapper = new Composite(rowWrapper, SWT.NONE);

        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(rowWrapper);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(rowWrapper);

        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(wrapper);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(wrapper);

        displayText = new LabelledText(wrapper, "Display Name");
        displayText.addModifyListener(this);

        locationText = new Label(wrapper, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(locationText);

    }

    public void validate() {
        if (StringUtils.isEmpty(displayText.getText())) {
            displayText.showError("Display Name is required");
        } else {
            displayText.hideError();
        }
        broadcaster.broadcastChange();
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
        locationText.setText(Path.fromOSString(result).lastSegment());
        locationText.setToolTipText(result);
    }

    public String getLocation() {
        return locationText.getToolTipText();
    }

    public String getDisplayName() {
        return displayText.getText();
    }

    public void setDisplayName(String displayName) {
        displayText.setText(displayName.replace("_", " "));
    }

    public boolean hasErrors() {
        return displayText.hasError();
    }

}
