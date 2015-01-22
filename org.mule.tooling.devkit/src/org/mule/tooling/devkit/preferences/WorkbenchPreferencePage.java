package org.mule.tooling.devkit.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mule.tooling.devkit.DevkitUIPlugin;

public class WorkbenchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public static String DEVKIT_DEBUG_MODE = "devkit.debug.mode";

    public WorkbenchPreferencePage() {
        super(GRID);
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(DevkitUIPlugin.getDefault().getPreferenceStore());

    }

    @Override
    protected void createFieldEditors() {
        addField(new BooleanFieldEditor(DEVKIT_DEBUG_MODE, "Run Goals In Debug Mode", getFieldEditorParent()));
    }

}
