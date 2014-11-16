package org.mule.tooling.devkit.treeview.model;

import org.mule.devkit.utils.NameUtils;

public class ModuleUtils {

    public static String getTargetNameSpace(Module module) {
        String prefix = "";
        for (Property prop : module.getProperties()) {
            if (prop.getName().equals("name")) {
                prefix = prop.getValue();
                break;
            }
        }
        return prefix;
    }

    public static String getMethodName(ModuleMethod method) {
        String methodName = NameUtils.uncamel(method.getMethod().getName().toString());
        for (Property prop : method.getProperties()) {
            if (prop.getName().equals("name")) {
                methodName = prop.getValue();
                break;
            }
        }
        return methodName;
    }
}
