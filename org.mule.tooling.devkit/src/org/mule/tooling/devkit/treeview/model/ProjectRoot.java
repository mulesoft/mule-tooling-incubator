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
        Collections.sort(modules, new Comparator<Module>() {

            @Override
            public int compare(Module o1, Module o2) {
                if (o1.getType().equals(o2))
                    return o1.getName().compareTo(o2.getName());
                if (o1.getType().equals("@Connector") || o1.getType().equals("@Module")) {
                    if (o2.getType().equals("@Connector") || o2.getType().equals("@Module")) {
                        return o1.getName().compareTo(o2.getName());
                    }
                    return -1;
                }
                return 1;
            }

        });
        return modules.toArray();
    }
}
