package org.mule.tooling.devkit.template;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.mule.tooling.devkit.DevkitUIPlugin;
import org.mule.tooling.devkit.template.replacer.Replacer;

public class TemplateStringWriter {

    private IProgressMonitor monitor;

    public TemplateStringWriter(IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    public String apply(final String templatePath, Replacer replacer) throws CoreException {

        Writer writer = new StringWriter();
        Reader reader = null;

        try {
            monitor.beginTask("Creating file Source Code", 100);

            InputStream pomTemplateResource;

            pomTemplateResource = TemplateStringWriter.class.getResourceAsStream(templatePath);

            reader = new InputStreamReader(pomTemplateResource, "UTF-8");

            replacer.replace(reader, writer);

            writer.flush();

            monitor.worked(30);

            return writer.toString();
        } catch (Exception e) {
            DevkitUIPlugin.getDefault().logError("Failed to generate file", e);
        } finally {
            monitor.done();
            if (writer != null) {
                IOUtils.closeQuietly(writer);
            }
            if (reader != null) {
                IOUtils.closeQuietly(reader);
            }
        }
        return null;
    }

}
