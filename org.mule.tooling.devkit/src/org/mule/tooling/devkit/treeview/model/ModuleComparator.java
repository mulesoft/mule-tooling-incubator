package org.mule.tooling.devkit.treeview.model;

import java.util.Comparator;

public class ModuleComparator implements Comparator<Module> {

    @Override
    public int compare(Module o1, Module o2) {
        if (o1.getType().equals(o2))
            return o1.getName().compareTo(o2.getName());
        if (isConnector(o1) && isConnector(o2))
            return o1.getName().compareTo(o2.getName());
        if (isConnector(o1))
            return -1;
        if (isConnector(o2))
            return 1;
        return o1.getName().compareTo(o2.getName());
    }

    private boolean isConnector(Module module) {
        return module.getType().equals("@" + ModelUtils.CONNECTOR_ANNOTATION.getName().toString())
                || module.getType().equals("@" + ModelUtils.MODULE_ANNOTATION.getName().toString());
    }
}
