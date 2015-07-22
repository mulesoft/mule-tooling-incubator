package org.mule.tooling.incubator.gradle.ui;

import java.util.Collection;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.ui.MuleUiConstants;

public class Utils {

    public static Text initializeTextField(Group groupBox, String labelText, String defaultValue, String tooltip, ModifyListener modifyListener) {
        Label label = new Label(groupBox, SWT.NULL);
        label.setText(labelText);
        label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(MuleUiConstants.LABEL_WIDTH, SWT.DEFAULT).create());
        Text textField = new Text(groupBox, SWT.BORDER);
        textField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textField.setText(defaultValue);
        textField.addModifyListener(modifyListener);
        textField.setToolTipText(tooltip);
        return textField;
    }

	public static AutoCompleteField initializeAutoCompleteField(Text text, Collection<?> keySet) {
		return new AutoCompleteField(text, new TextContentAdapter(), keySet.toArray(new String[0]));
	}
}
