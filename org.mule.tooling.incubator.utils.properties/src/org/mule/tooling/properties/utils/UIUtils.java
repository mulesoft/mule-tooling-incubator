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
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mule.tooling.properties.Activator;
import org.mule.tooling.properties.actions.ContributedAction;
import org.mule.tooling.properties.editors.MultiPagePropertiesEditorContributor;

public class UIUtils {

	public static ImageDescriptor IMG_ENCRYPT = AbstractUIPlugin
			.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
					"icons/encrypted-icon-16x16.gif");
	
	public static final String EDITOR_TOOLBARS_EXTENSION_ID = "org.mule.tooling.incubator.utils.properties.editortoolbar";
	
	
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
	
}
