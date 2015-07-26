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
package org.mule.tooling.properties.editors;

import java.util.Map;
import java.util.Properties;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author seba
 *
 */
public class PropertiesTableContentProvider implements
		IStructuredContentProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		/**this.model = 
		this.viewer = (TableViewer) viewer;
		if (manager != null)
			manager.removeFavoritesManagerListener(this);
		manager = (FavoritesManager) newInput;
		if (manager != null)
			manager.addFavoritesManagerListener(this);**/
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		Properties properties = (Properties)inputElement;
		return properties.entrySet().toArray(new Map.Entry[properties.size()]);
	}

}
