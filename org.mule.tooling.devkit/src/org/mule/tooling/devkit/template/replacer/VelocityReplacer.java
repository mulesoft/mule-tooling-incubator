package org.mule.tooling.devkit.template.replacer;

import java.io.Reader;
import java.io.Writer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.VelocityException;

public abstract class VelocityReplacer implements Replacer {

    @Override
    public void replace(Reader reader, Writer writer) throws Exception {
        ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
        ClassLoader classLoader = VelocityReplacer.class.getClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            try {
                Velocity.init();
            } catch (Exception e) {
                throw new VelocityException(e);
            }

            VelocityContext context = new VelocityContext();

            populateContext(context);

            boolean evaluate = Velocity.evaluate(context, writer, "velocity class rendering", reader);

            if (evaluate == false) {
                throw new Exception("Evaluation of the template failed.");
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassloader);
        }
    }

    protected abstract void populateContext(VelocityContext context);
}
