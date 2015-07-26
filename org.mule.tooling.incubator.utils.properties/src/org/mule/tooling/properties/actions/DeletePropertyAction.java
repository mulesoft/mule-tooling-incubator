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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.properties.editors.IPropertiesEditor;
import org.mule.tooling.properties.editors.IPropertiesEditorAccessor;

/**
 * @author seba
 *
 */
public class DeletePropertyAction extends Action {

	public static final String REMOVE_PROPERTY = "Remove property";
	private final IPropertiesEditorAccessor provider;
	

	public DeletePropertyAction(IPropertiesEditorAccessor provider) {

		super();
		this.provider = provider;
		setToolTipText(REMOVE_PROPERTY);
		setText(REMOVE_PROPERTY);
		
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE));
		setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));


	}
	
	@Override
	public void run() {
		
		if (provider.getPropertiesEditor().getSelectedProperty() == null){
			return;
		}

		boolean openConfirm = MessageDialog.openConfirm(Display
				.getCurrent().getActiveShell(), "Remove property", "Are you sure you want to remove the selected property?");
		if (openConfirm){
			IPropertiesEditor page = provider.getPropertiesEditor();
			
			page.removeProperty(String.valueOf(page.getSelectedProperty().getKey()));
		}
	}

}
