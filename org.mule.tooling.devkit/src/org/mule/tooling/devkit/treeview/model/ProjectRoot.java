package org.mule.tooling.devkit.treeview.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProjectRoot extends DefaultNodeItem {

    public ProjectRoot() {
        super(null, null, null);
    }

    private List<Module> modules = new ArrayList<Module>();

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    @Override
    public Object[] getChildren() {
        Collections.sort(modules, new ModuleComparator());
        return modules.toArray();
    }
}
