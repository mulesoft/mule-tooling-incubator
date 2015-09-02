package org.mule.tooling.devkit.ui;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class MessageDecorator extends Composite {

    private Label label;
    private boolean visible = false;
    private Composite emptyComposite;
    private StackLayout stacklayout;

    public MessageDecorator(Composite parent, int style) {
        super(parent, style);
        createControl();
    }

    private void createControl() {
        stacklayout = new StackLayout();
        stacklayout.marginWidth = 0;
        stacklayout.marginHeight = 0;
        setLayout(stacklayout);

        label = new Label(this, SWT.BOTTOM);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.END).grab(false, false).applyTo(label);

        emptyComposite = new Composite(this, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(emptyComposite);
        GridDataFactory.fillDefaults().applyTo(emptyComposite);

        stacklayout.topControl = emptyComposite;
    }

    public void setImage(Image displayImage) {
        if (displayImage != null) {
            label.setImage(displayImage);
        }
    }

    public void setDescriptionText(String string) {
        if (StringUtils.isNotBlank(string)) {
            label.setToolTipText(string);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void hide() {
        if (visible) {
            visible = false;
            stacklayout.topControl = emptyComposite;
            layout();
        }
    }

    public void show() {
        if (!visible) {
            visible = true;
            stacklayout.topControl = label;
            layout();
        }
    }

    @Override
    public void dispose() {
        label.dispose();
        emptyComposite.dispose();
        super.dispose();
    }
}
