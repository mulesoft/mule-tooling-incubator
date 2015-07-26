/**
 * $Id: LicenseManager.java 10480 2007-12-19 00:47:04Z moosa $
 * --------------------------------------------------------------------------------------
 * (c) 2003-2008 MuleSource, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSource's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSource. If such an agreement is not in place, you may not use the software.
 */

package org.mule.tooling.properties.editors;

import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertiesFileEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

public class MulePropertiesEditor extends PropertiesFileEditor implements IPropertyChangeListener{
	
	public IDocument getPropertiesDocument(){
		return getSourceViewer().getDocument();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		
		getPropertiesDocument().set((String) event.getNewValue());
	}
	
	@Override
	public void createPartControl(final Composite parent) {
		
		super.createPartControl(parent);
		setPartName("Text editor");
	}
	

}
