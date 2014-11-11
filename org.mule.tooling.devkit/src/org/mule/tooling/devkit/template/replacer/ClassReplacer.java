package org.mule.tooling.devkit.template.replacer;

import org.apache.velocity.VelocityContext;
import org.mule.tooling.devkit.common.ConnectorMavenModel;
import org.mule.tooling.devkit.common.DevkitUtils;

public class ClassReplacer extends VelocityReplacer {

    private String packageName;
    private String moduleName;
    private String className;
    private boolean metadataEnabled;
    private String runtimeId;
    private ConnectorMavenModel model;
    public ClassReplacer(String packageName, String moduleName, String className, String runtimeId, boolean metadataEnabled, ConnectorMavenModel model) {
        this.packageName = packageName;
        this.moduleName = moduleName;
        this.className = className;
        this.metadataEnabled = metadataEnabled;
        this.runtimeId = runtimeId;
        this.model = model;

    }

    @Override
    protected void doReplace(VelocityContext context) {
        context.put("package", packageName);
        context.put("connectorName", moduleName);
        context.put("moduleName", DevkitUtils.toConnectorName(moduleName));
        context.put("className", className);
        context.put("metadataEnabled", metadataEnabled);
        context.put("runtimeId", runtimeId);
        context.put("project", model);
    }

}
