/**
 * $Id: LicenseManager.java 10480 2007-12-19 00:47:04Z moosa $
 * --------------------------------------------------------------------------------------
 * (c) 2003-2008 MuleSource, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSource's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSource. If such an agreement is not in place, you may not use the software.
 */

/**
 * 
 */
package org.mule.tooling.properties.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.properties.dialogs.AddPropertyDialog;
import org.mule.tooling.properties.editors.IPropertiesEditorAccessor;

/**
 * @author Sebastian Sampaoli
 * 
 */
public class AddPropertyAction extends Action {

	public static final String ADD_A_NEW_PROPERTY = "Add a new property";
	private final IPropertiesEditorAccessor provider;
	

	/**
	 * @param iWorkbenchPage
	 * 
	 */
	public AddPropertyAction(IPropertiesEditorAccessor provider) {
		super();
		this.provider = provider;
		
		setToolTipText(ADD_A_NEW_PROPERTY);
		setText(ADD_A_NEW_PROPERTY);
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_ADD));

	}

	@Override
	public void run() {

		AddPropertyDialog addPropertyDialog = new AddPropertyDialog(Display
				.getCurrent().getActiveShell(), provider);
		int id = addPropertyDialog.open();
		if (id == IDialogConstants.OK_ID) {
			provider.getPropertiesEditor().addProperty(
					addPropertyDialog.getKey(), addPropertyDialog.getValue());

		}
	}
}
