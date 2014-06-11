package org.mule.tooling.ui.contribution.munit.runner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;

/**
 * <p>
 * The UI that shows the failing tests of the Munit suite
 * </p>
 */
public class MunitCounterPanel extends Composite {

    protected Text fNumberOfErrors;
    protected Text fNumberOfFailures;
    protected Text fNumberOfRuns;
    protected int fTotal;
    protected int fIgnoredCount;

    public MunitCounterPanel(Composite parent) {
        super(parent, SWT.WRAP);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 9;
        gridLayout.makeColumnsEqualWidth = false;
        gridLayout.marginWidth = 0;
        setLayout(gridLayout);

        fNumberOfRuns = createLabel("Run:", null, " 0/0  ");
        fNumberOfErrors = createLabel("Errors:", MunitPlugin.fErrorIcon, " 0 ");
        fNumberOfFailures = createLabel("Failures:", MunitPlugin.fFailureIcon, " 0 ");

    }

    private Text createLabel(String name, Image image, String init) {
        Label label = new Label(this, SWT.NONE);
        if (image != null) {
            image.setBackground(label.getBackground());
            label.setImage(image);
        }
        label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

        label = new Label(this, SWT.NONE);
        label.setText(name);
        label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

        Text value = new Text(this, SWT.READ_ONLY);
        value.setText(init);
        value.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        value.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING));
        return value;
    }

    public void reset() {
        setErrorValue(0);
        setFailureValue(0);
        setRunValue(0, 0);
        fTotal = 0;
    }

    public void setTotal(int value) {
        fTotal = value;
    }

    public int getTotal() {
        return fTotal;
    }

    public void setRunValue(int value, int ignoredCount) {
        String runString;
        if (ignoredCount == 0)
            runString = String.format("%s/%s", Integer.toString(value), Integer.toString(fTotal));
        else
            runString = String.format("%s/%s/%s", Integer.toString(value), Integer.toString(fTotal), Integer.toString(ignoredCount));
        fNumberOfRuns.setText(runString);

        if (fIgnoredCount == 0 && ignoredCount > 0 || fIgnoredCount != 0 && ignoredCount == 0) {
            layout();
        } else {
            fNumberOfRuns.redraw();
            redraw();
        }
        fIgnoredCount = ignoredCount;
    }

    public void setErrorValue(int value) {
        fNumberOfErrors.setText(Integer.toString(value));
        redraw();
    }

    public void setFailureValue(int value) {
        fNumberOfFailures.setText(Integer.toString(value));
        redraw();
    }
}
