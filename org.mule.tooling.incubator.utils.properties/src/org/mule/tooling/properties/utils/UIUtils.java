/**
 * $Id: LicenseManager.java 10480 2007-12-19 00:47:04Z moosa $
 * --------------------------------------------------------------------------------------
 * (c) 2003-2008 MuleSource, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSource's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSource. If such an agreement is not in place, you may not use the software.
 */

package org.mule.tooling.properties.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mule.tooling.properties.Activator;
import org.mule.tooling.properties.actions.ContributedAction;
import org.mule.tooling.properties.editors.MultiPagePropertiesEditorContributor;
import org.mule.tooling.properties.extension.IPropertyKeyCompletionContributor;
import org.mule.tooling.properties.extension.PropertyKeySuggestion;
import org.mule.tooling.ui.modules.core.ModulesUICoreImages;

public class UIUtils {

	public static ImageDescriptor IMG_ENCRYPT = AbstractUIPlugin
			.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
					"icons/encrypted-icon-16x16.gif");
	
	public static final String EDITOR_TOOLBARS_EXTENSION_ID = "org.mule.tooling.incubator.utils.properties.editortoolbar";
	
	public static final String EDITOR_COMPLETION_EXTENSION_ID = "org.mule.tooling.incubator.utils.properties.keycompletion";
	
	
	/**
	 * Shows JFace ErrorDialog but improved by constructing full stack trace in
	 * detail area.
	 */
	public static void showException(String title, String msg, Throwable t) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);

		final String trace = sw.toString(); // stack trace as a string

		// Temp holder of child statuses
		List<Status> childStatuses = new ArrayList<Status>();

		// Split output by OS-independend new-line
		for (String line : trace.split(System.getProperty("line.separator"))) {
			// build & add status
			childStatuses.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					line));
		}

		MultiStatus ms = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR,
				childStatuses.toArray(new Status[] {}), // convert to array of
														// statuses
				t.getLocalizedMessage(), t);

		ErrorDialog.openError(Display.getCurrent().getActiveShell(), title,
				msg, ms);
	}
	
	
	public static List<Action> getContributedToolbarButtons(MultiPagePropertiesEditorContributor editor) {
		
		
		List<Action> ret = new ArrayList<Action>();
		
		IConfigurationElement[] configs = Platform.getExtensionRegistry().getConfigurationElementsFor(EDITOR_TOOLBARS_EXTENSION_ID);
		
		for(IConfigurationElement config : configs) {
			ret.add(new ContributedAction(config, editor));
		}
		
		return ret;
	}
	
	public static AutoCompleteField initializeAutoCompleteField(Text text, Collection<?> keySet) {
		return new AutoCompleteField(text, new TextContentAdapter(), keySet.toArray(new String[0]));
	}
	
	public static List<PropertyKeySuggestion> getContributedSuggestions(IResource currentPropertiesFile) {
		
		HashSet<PropertyKeySuggestion> suggestions = new HashSet<PropertyKeySuggestion>();
		
		IConfigurationElement[] configs = Platform.getExtensionRegistry().getConfigurationElementsFor(EDITOR_COMPLETION_EXTENSION_ID);
		
		for(IConfigurationElement config : configs) {
			suggestions.addAll(executeCompletionExtensionPoint(config, currentPropertiesFile));
		}
		
		return new ArrayList<PropertyKeySuggestion>(suggestions);
	}
	
	private static List<PropertyKeySuggestion> executeCompletionExtensionPoint(IConfigurationElement config, IResource currentFile) {
		try {
			
			Object o = config.createExecutableExtension("class");
			
			if (o instanceof IPropertyKeyCompletionContributor) {
				return ((IPropertyKeyCompletionContributor) o).buildSuggestions(currentFile);
			}
			
			return Collections.emptyList();
			
		} catch (Exception ex) {
			//TODO - log
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	
    public static CompletionProposal build(PropertyKeySuggestion suggestion, int offset, int currentWordLength) {
        
        String completion = suggestion.getSuggestion() + "=" + suggestion.getDefaultValue();
        String displayName = completion;
        String imgCode = ModulesUICoreImages.AUTOCOMLETE_ATTRIBUTE;
        Image image = ModulesUICoreImages.getImage(imgCode);
        int completionLength = StringUtils.length(completion);
        
        return new CompletionProposal(
                completion, 
                offset - currentWordLength, 
                currentWordLength, 
                completionLength,
                image,
                displayName,
                null,
                suggestion.getDescription());
    }




}
