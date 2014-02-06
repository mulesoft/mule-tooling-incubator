package org.mule.tooling.ui.contribution.munit.editors.production;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mule.tooling.model.messageflow.Property;
import org.mule.tooling.model.messageflow.PropertyCollection;

public class PropertiesViewerLabelProvider extends LabelProvider{

	@Override
	public Image getImage(Object arg0) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.mule.tooling.messageflow", "icons/bullet-blue-alt.png").createImage();
	}

	@Override
	public String getText(Object arg0) {
		if ( arg0 instanceof Property ){
			Property prop = (Property) arg0;
			if ( !prop.getName().isEmpty() && !prop.getValue().isEmpty()){
				return prop.getName() + ":" + prop.getValue();

			}
			else if (!prop.getName().isEmpty()){
				return prop.getName();
			}
		}
		else if ( arg0 instanceof PropertyCollection){
			return ((PropertyCollection) arg0).getName();
		}
		return null;
	}

}