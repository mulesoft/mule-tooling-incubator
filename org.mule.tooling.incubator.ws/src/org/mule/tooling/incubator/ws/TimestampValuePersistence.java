
package org.mule.tooling.incubator.ws;

import org.mule.tooling.model.messageflow.Property;
import org.mule.tooling.model.messageflow.PropertyCollection;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;
import org.mule.tooling.ui.modules.core.widgets.IValueConverter;
import org.mule.tooling.ui.modules.core.widgets.meta.INestedElementChooser;

import java.util.List;

public final class TimestampValuePersistence implements INestedElementChooser, IValueConverter
{

    private static final String checkbox = "enableTimestamp";
    private static final String watermarkId = "@http://www.mulesoft.org/schema/mule/ws2/wss-timestamp";
    private static final String suffix = ";1";

    @Override
    public String getId(final PropertyCollectionMap newProperties, PropertyCollectionMap parentProperties)
    {
        if (selected(newProperties, checkbox))
        {
            newProperties.removeProperty(checkbox);
            return watermarkId + suffix;
        }
        else
        {
            return "";
        }
    }

    @Override
    public PropertyCollection adjust(final List<PropertyCollection> defs2)
    {
        for (PropertyCollection ca : defs2) {
            if (ca.getName().startsWith(watermarkId)) {
                ca.getProperties().add(createProperty(checkbox, "true"));
                return ca;
            }
        }

        final PropertyCollection propertyCollection2 = new PropertyCollection();
        return propertyCollection2;
    }

    private Property createProperty(final String name, final String value)
    {
        final Property property = new Property();
        property.setName(name);
        property.setValue(value);
        return property;
    }

    private boolean selected(final PropertyCollectionMap map, final String strategy)
    {
        return map.getProperty(strategy, "").equals("true");
    }

    @Override
    public String convertModelToXML(final String str)
    {
        return null;
    }

    @Override
    public String convertXMLToModel(final String str)
    {
        return null;
    }
}
