package org.mule.tooling.devkit.template.replacer;

import org.apache.velocity.VelocityContext;

public class ClassReplacer extends VelocityReplacer {

    private String packageName;
    private String moduleName;
    private String className;
	private boolean metadataEnabled;

    public ClassReplacer(String packageName, String moduleName, String className, boolean metadataEnabled) {
        this.packageName = packageName;
        this.moduleName = moduleName;
        this.className = className;
		this.metadataEnabled = metadataEnabled;
        
    }

    @Override
    protected void doReplace(VelocityContext context) {
        context.put("package", packageName);
        context.put("moduleName", moduleName.toLowerCase());
        context.put("className", className);
        context.put("metadataEnabled", metadataEnabled);
    }

}
