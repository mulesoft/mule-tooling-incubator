package org.mule.tooling.devkit.template.replacer;

import java.util.Map;

import org.apache.velocity.VelocityContext;

public class ComponentReplacer extends VelocityReplacer {

    private Map<String, Object> model;

    public ComponentReplacer(Map<String, Object> model) {
        this.model = model;

    }

    @Override
    protected void populateContext(VelocityContext context) {
        context.put("project", model);
    }

    public void update(String key, Object value) {
        model.put(key, value);
    }
}
