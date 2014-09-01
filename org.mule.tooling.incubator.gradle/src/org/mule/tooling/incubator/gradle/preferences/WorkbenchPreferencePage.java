package org.mule.tooling.incubator.gradle.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mule.tooling.incubator.gradle.Activator;

public class WorkbenchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public static String GRADLE_HOME_ID = "gradle.home";

    public WorkbenchPreferencePage() {
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    @Override
    protected void createFieldEditors() {
        addField(new DirectoryFieldEditor(GRADLE_HOME_ID, "Home", getFieldEditorParent()));
    }

}
