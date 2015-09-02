package org.mule.tooling.devkit.treeview.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.swt.graphics.Image;
import org.mule.devkit.utils.NameUtils;
import org.mule.tooling.devkit.DevkitImages;

public class ModuleMethod extends DefaultNodeItem {

    public ModuleMethod(NodeItem parent, ICompilationUnit cu, ASTNode node) {
        super(parent, cu, node);
    }

    private boolean isConnectionMethod;

    private boolean isMetadataMethod;

    private MethodDeclaration method;

    private List<Property> properties = new ArrayList<Property>();

    public boolean isConnectionMethod() {
        return isConnectionMethod;
    }

    public void setConnectionMethod(boolean isConnectionMethod) {
        this.isConnectionMethod = isConnectionMethod;
    }

    public MethodDeclaration getMethod() {
        return method;
    }

    public void setMethod(MethodDeclaration method) {
        this.method = method;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public boolean isMetadataMethod() {
        return isMetadataMethod;
    }

    public void setMetadataMethod(boolean isMetadataMethod) {
        this.isMetadataMethod = isMetadataMethod;
    }

    @Override
    public String getLabel() {
        MethodDeclaration method = getMethod();
        StringBuilder params = new StringBuilder();
        params.append("(");
        List<String> types = new ArrayList<String>();
        for (Object parameter : method.parameters()) {
            VariableDeclaration variableDeclaration = (VariableDeclaration) parameter;
            String type = variableDeclaration.getStructuralProperty(SingleVariableDeclaration.TYPE_PROPERTY).toString();
            for (int i = 0; i < variableDeclaration.getExtraDimensions(); i++) {
                type += "[]";
            }
            types.add(type);
        }
        if (!types.isEmpty())
            params.append(StringUtils.join(types, ","));
        params.append(")");

        return method.getName().toString() + params + ":" + method.getReturnType2().toString();
    }

    @Override
    public Image getImage() {
        if (isConnectionMethod()) {
            return DevkitImages.getManagedImage("", "connectivity.gif");
        }
        if (isMetadataMethod()) {
            return DevkitImages.getManagedImage("", "metadata.gif");
        }
        return DevkitImages.getManagedImage("", "processor.gif");
    }

    @Override
    public Object[] getChildren() {

        List<Object> list = new ArrayList<Object>();
        boolean hasName = false;
        for (Property prop : getProperties()) {
            if (prop.getName().equals("name"))
                hasName = true;
        }
        if (!hasName && !(isConnectionMethod() || isMetadataMethod())) {
            Property property = new Property(this, cu, node);
            property.setName("XSD Name");
            property.setValue(NameUtils.uncamel(getMethod().getName().toString()));
            list.add(property);
        } else {
            for (Property pro : getProperties()) {
                if (pro.getName().equals("name")) {
                    Property property = new Property(this, cu, node);
                    property.setName("XSD Name");
                    property.setValue(pro.getValue());
                    list.add(property);
                } else {
                    list.add(pro);
                }
            }

        }
        return list.toArray();
    }
}
