package org.mule.tooling.devkit.template.replacer;

import org.apache.velocity.VelocityContext;

public class ClassReplacer extends VelocityReplacer {

    private String packageName;
    private String moduleName;
    private String className;

    public ClassReplacer(String packageName, String moduleName, String className) {
        this.packageName = packageName;
        this.moduleName = moduleName;
        this.className = className;
    }

    @Override
    protected void doReplace(VelocityContext context) {
        context.put("package", packageName);
        context.put("moduleName", moduleName.toLowerCase());
        context.put("className", className);
    }

}
