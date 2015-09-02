package org.mule.tooling.devkit.treeview.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.devkit.DevkitImages;

public class Module extends DefaultNodeItem implements Comparable<Module> {

    public Module(NodeItem parent, ICompilationUnit cu, ASTNode node) {
        super(parent, cu, node);
    }

    private String name;
    private String type;
    private List<Property> properties = new ArrayList<Property>();
    private List<ModuleMethod> processor = new ArrayList<ModuleMethod>();
    private List<ModuleField> configurable = new ArrayList<ModuleField>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ModuleMethod> getProcessor() {
        return processor;
    }

    public void setProcessor(List<ModuleMethod> processor) {
        this.processor = processor;
    }

    public List<ModuleField> getConfigurable() {
        return configurable;
    }

    public void setConfigurable(List<ModuleField> configurable) {
        this.configurable = configurable;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getLabel() {
        return name + (StringUtils.isEmpty(getType()) ? "" : ": " + getType());
    }

    @Override
    public Image getImage() {

        return DevkitImages.getManagedImage("", "module.gif");
    }

    @Override
    public Object[] getChildren() {

        List<Object> list = new ArrayList<Object>();

        list.addAll(getProperties());
        list.addAll(getConfigurable());
        list.addAll(getProcessor());

        return list.toArray();
    }

    @Override
    public int compareTo(Module arg0) {
        if (this.getType() == null)
            return -1;
        if (arg0.getType() == null)
            return -1;
        if (this.getType().equals(arg0.getType())) {
            return this.getName().compareTo(arg0.getName());
        }
        return this.getType().compareTo(arg0.getType());
    }

}
