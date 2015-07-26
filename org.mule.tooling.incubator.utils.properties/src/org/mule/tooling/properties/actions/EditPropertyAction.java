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

import java.util.Map.Entry;

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
public class EditPropertyAction extends Action {

	public static final String EDIT_PROPERTY = "Edit property";
	private final IPropertiesEditorAccessor provider;

	/**
	 * @param iWorkbenchPage
	 * 
	 */
	public EditPropertyAction(IPropertiesEditorAccessor provider) {
		super();
		this.provider = provider;

		setToolTipText(EDIT_PROPERTY);
		setText(EDIT_PROPERTY);
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));

	}

	@Override
	public void run() {

		Entry selectedProperty = provider.getPropertiesEditor()
				.getSelectedProperty();

		if (selectedProperty != null) {
			AddPropertyDialog addPropertyDialog = new AddPropertyDialog(Display
					.getCurrent().getActiveShell(), provider);
			String originalKey = String.valueOf(selectedProperty.getKey());
			addPropertyDialog.setKey(originalKey);
			String originalValue = String.valueOf(selectedProperty.getValue());
			addPropertyDialog.setValue(originalValue);
			int id = addPropertyDialog.open();
			if (id == IDialogConstants.OK_ID) {
				provider.getPropertiesEditor().removeProperty(originalKey);
				provider.getPropertiesEditor().addProperty(
						addPropertyDialog.getKey(),
						addPropertyDialog.getValue());

			}
		}
	}
}
