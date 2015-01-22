package org.mule.tooling.devkit.template.replacer;

import java.io.Reader;
import java.io.Writer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public abstract class VelocityReplacer implements Replacer {

    @Override
    public void replace(Reader reader, Writer writer) throws Exception {
        Velocity.init();
        VelocityContext context = new VelocityContext();

        populateContext(context);

        boolean evaluate = Velocity.evaluate(context, writer, "velocity class rendering", reader);

        if (evaluate == false) {
            throw new Exception("Evaluation of the template failed.");
        }

    }

    protected abstract void populateContext(VelocityContext context);
}
