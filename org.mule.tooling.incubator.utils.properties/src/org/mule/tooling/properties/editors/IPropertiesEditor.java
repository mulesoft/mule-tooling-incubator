/**
 * $Id: LicenseManager.java 10480 2007-12-19 00:47:04Z moosa $
 * --------------------------------------------------------------------------------------
 * (c) 2003-2008 MuleSource, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSource's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSource. If such an agreement is not in place, you may not use the software.
 */

package org.mule.tooling.properties.editors;

import java.util.Collection;
import java.util.Map;

public interface IPropertiesEditor {
	
	void addProperty(String key, Object value);

	void removeProperty(String keyValue);
	
	Map.Entry getSelectedProperty();

	void updateProperty(String key, Object value);
	
	Collection<String> buildKeySuggestions();
	
}
