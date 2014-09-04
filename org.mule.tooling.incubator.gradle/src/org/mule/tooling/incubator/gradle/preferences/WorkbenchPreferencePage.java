package org.mule.tooling.incubator.gradle.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mule.tooling.incubator.gradle.Activator;

public class WorkbenchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public static String GRADLE_HOME_ID = "gradle.home";
    public static String GRADLE_PLUGIN_VERSION_ID = "gradle.plugin.version";
    
    
    public WorkbenchPreferencePage() {
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    @Override
    protected void createFieldEditors() {
        addField(new DirectoryFieldEditor(GRADLE_HOME_ID, "Home", getFieldEditorParent()));
        addField(new StringFieldEditor(GRADLE_PLUGIN_VERSION_ID, "Plugin Version", getFieldEditorParent()));
    }

}
