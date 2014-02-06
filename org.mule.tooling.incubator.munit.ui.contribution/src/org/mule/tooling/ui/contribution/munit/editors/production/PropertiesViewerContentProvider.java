package org.mule.tooling.ui.contribution.munit.editors.production;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mule.tooling.model.messageflow.Property;
import org.mule.tooling.model.messageflow.PropertyCollection;

/**
 * <p>
 * Content provider for the Properties viewer of the produciton view
 * </p> 
 */
public class PropertiesViewerContentProvider implements ITreeContentProvider{

	@Override
	public Object[] getElements(Object arg0) {
		List<Object> properties = new ArrayList<Object>();
		PropertyCollection coll = (PropertyCollection) arg0;
		for ( Property property :  coll.getProperties() ){
			if ( property.getName() != null && property.getValue() != null )
			{
				properties.add(property);
			}
		}

		if ( properties.isEmpty() ){
			properties.add(new Property("No visible properties", ""));
		}
		return properties.toArray();
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getChildren(Object arg0) {
		PropertyCollection coll = (PropertyCollection) arg0;
		return coll.getProperties().toArray();
	}

	@Override
	public Object getParent(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object arg0) {
		return arg0 instanceof PropertyCollection;
	}
	
}
