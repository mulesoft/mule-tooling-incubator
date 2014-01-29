package org.mule.tooling.devkit.template.replacer;

import org.apache.velocity.VelocityContext;

public class ClassReplacer extends VelocityReplacer {

    private String packageName;
    private String moduleName;
    private String className;
	private boolean metadataEnabled;
	private String runtimeId;
    public ClassReplacer(String packageName, String moduleName, String className,String runtimeId, boolean metadataEnabled) {
        this.packageName = packageName;
        this.moduleName = moduleName;
        this.className = className;
		this.metadataEnabled = metadataEnabled;
		this.runtimeId = runtimeId;
        
    }

    @Override
    protected void doReplace(VelocityContext context) {
        context.put("package", packageName);
        context.put("connectorName", moduleName);
        context.put("moduleName", moduleName.toLowerCase());
        context.put("className", className);
        context.put("metadataEnabled", metadataEnabled);
        context.put("runtimeId", runtimeId);
    }

}
