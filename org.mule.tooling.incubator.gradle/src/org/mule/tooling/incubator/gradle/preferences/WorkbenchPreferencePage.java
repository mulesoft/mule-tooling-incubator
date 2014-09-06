package org.mule.tooling.incubator.gradle.preferences;

import java.io.File;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mule.tooling.incubator.gradle.Activator;
import org.mule.tooling.incubator.gradle.GradlePluginConstants;
import org.mule.tooling.incubator.gradle.GradlePluginUtils;

public class WorkbenchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public static String GRADLE_HOME_ID = "gradle.home";
    public static String GRADLE_PLUGIN_VERSION_ID = "gradle.plugin.version";
    public static String GRADLE_VERSION_ID = "gradle.version";
    
    ComboFieldEditor versionsEditor;
    DirectoryFieldEditor gradleHomeField;
    private boolean shouldUseGradleHome;
    
    private static String[][] AVAILABLE_VERSIONS = {{"2.0", "2.0"}, {"1.12 (recommended)", "1.12"}, {"Custom", GradlePluginConstants.USE_GRADLE_HOME_VERSION_VALUE}};
    
    public WorkbenchPreferencePage() {
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        
        //verify if we should use the gradle home.
        shouldUseGradleHome = GradlePluginConstants.USE_GRADLE_HOME_VERSION_VALUE.equals(getPreferenceStore().getString(GRADLE_VERSION_ID));
    }

    @Override
    protected void createFieldEditors() {
        
    	versionsEditor = new ComboFieldEditor(GRADLE_VERSION_ID, "Gradle Version", AVAILABLE_VERSIONS, getFieldEditorParent());    	
    	gradleHomeField = new DirectoryFieldEditor(GRADLE_HOME_ID, "Home", getFieldEditorParent());
    	gradleHomeField.setErrorMessage("Yoy must specify a valid gradle installation");
    	gradleHomeField.setEmptyStringAllowed(!shouldUseGradleHome);
    	
    	addField(versionsEditor);
    	addField(gradleHomeField);
        addField(new StringFieldEditor(GRADLE_PLUGIN_VERSION_ID, "Plugin Version", getFieldEditorParent()));
        
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent event) {
    	super.propertyChange(event);
    	
    	if (event.getSource() != versionsEditor) {
    		checkState();
    		return;
    	}
    	
    	shouldUseGradleHome = event.getNewValue().equals(GradlePluginConstants.USE_GRADLE_HOME_VERSION_VALUE);
    	gradleHomeField.setEmptyStringAllowed(!shouldUseGradleHome);
    	checkState();
    }

	@Override
	protected void checkState() {
		super.checkState();
		File gradleHome = new File(gradleHomeField.getStringValue());
		//should I perform validations here?
		if (!GradlePluginUtils.isFileValidGradleInstallation(gradleHome) && shouldUseGradleHome) {
			setValid(false);
			setErrorMessage(gradleHomeField.getErrorMessage());
			return;
		}
		
		setErrorMessage(null);
		setValid(true);
	}
    
}
