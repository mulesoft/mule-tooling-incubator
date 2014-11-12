package org.mule.tooling.devkit.template.replacer;

import org.apache.velocity.VelocityContext;
import org.mule.tooling.devkit.common.ConnectorMavenModel;

public class ClassReplacer extends VelocityReplacer {

    private ConnectorMavenModel model;
    public ClassReplacer(ConnectorMavenModel model) {
        this.model = model;

    }

    @Override
    protected void populateContext(VelocityContext context) {
        context.put("project", model);
    }

}
