package org.mule.tooling.devkit.ui;

import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class LabelledText extends Composite {

    private Text textControl;
    private Label labelControl;
    private MessageDecorator errorDecoration;
    private String labelString;

    public LabelledText(Composite parent, String label) {
        super(parent, SWT.NONE);
        this.labelString = label;
        createControl();
    }

    private void createControl() {
        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(this);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

        labelControl = new Label(this, SWT.NONE);
        labelControl.setText(labelString + ":");
        GridDataFactory.fillDefaults().applyTo(labelControl);

        errorDecoration = new MessageDecorator(this, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.END).grab(false, false).applyTo(errorDecoration);
        errorDecoration.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage());
        textControl = new Text(this, SWT.FILL | SWT.BORDER | SWT.SINGLE);

        GridDataFactory.fillDefaults().grab(true, false).applyTo(textControl);

        errorDecoration.show();
    }

    public String getText() {
        if (textControl.isDisposed()) {
            return "";
        }
        return textControl.getText();
    }

    public void setText(String string) {
        if (!textControl.isDisposed()) {
            textControl.setText(string);
        }
    }

    public void showError(String errorDescription) {
        errorDecoration.setDescriptionText(errorDescription);
        errorDecoration.show();
    }

    public void hideError() {
        errorDecoration.hide();
    }

    public void addModifyListener(ModifyListener listener) {
        textControl.addModifyListener(listener);
    }
    
    public boolean hasError(){
        return errorDecoration.isVisible();
    }
}
