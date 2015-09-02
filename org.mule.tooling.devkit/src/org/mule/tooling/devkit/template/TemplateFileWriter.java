package org.mule.tooling.devkit.template;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.mule.tooling.devkit.DevkitUIPlugin;
import org.mule.tooling.devkit.template.replacer.Replacer;

public class TemplateFileWriter {

    private IProject project;

    public TemplateFileWriter(IProject project) {
        this.project = project;
    }

    public void apply(final String templatePath, final String resultPath, Replacer replacer, IProgressMonitor monitor) throws CoreException {
        final IFile fileToCreate = project.getProject().getFile(resultPath);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Writer writer = null;
        Reader reader = null;

        try {
            monitor.beginTask("Creating file " + resultPath, 10);

            InputStream pomTemplateResource;

            pomTemplateResource = TemplateFileWriter.class.getResourceAsStream(templatePath);

            reader = new InputStreamReader(pomTemplateResource, "UTF-8");
            writer = new OutputStreamWriter(byteArrayOutputStream, "UTF-8");

            replacer.replace(reader, writer);

            writer.flush();

            monitor.worked(3);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            fileToCreate.create(byteArrayInputStream, false, new SubProgressMonitor(monitor, 4));
            fileToCreate.setDerived(false, new SubProgressMonitor(monitor, 3));

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

    }

}
