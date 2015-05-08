package org.mule.tooling.devkit.builder;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.mule.tooling.devkit.common.ConnectorMavenModel;
import org.mule.tooling.devkit.template.TemplateFileWriter;
import org.mule.tooling.devkit.template.replacer.ClassReplacer;
import org.mule.tooling.devkit.template.replacer.ComponentReplacer;
import org.mule.tooling.devkit.template.replacer.VelocityReplacer;

public class ComponentFactory {

    private static final String CONFIGURATION_TEMPLATE = "/templates/connector_basic.tmpl";

    public static File createConfigurationStrategy(IProject project, ConnectorMavenModel model, String location, IProgressMonitor monitor) throws CoreException {

        TemplateFileWriter writer = new TemplateFileWriter(project, new NullProgressMonitor());
        writer.apply(CONFIGURATION_TEMPLATE, location, new ClassReplacer(model));

        return project.getFile(location).getRawLocation().toFile();
    }

    public static String createBasicStrategy(Map<String, Object> model) throws CoreException {
        InputStream templateResource = TemplateFileWriter.class.getResourceAsStream("/templates/strategy-basic.tmpl");
        StringWriter writer = new StringWriter();
        Reader reader = null;
        ComponentReplacer replacer = new ComponentReplacer(model);
        try {
            reader = new InputStreamReader(templateResource, "UTF-8");
            replacer.replace(reader, writer);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return writer.toString();
    }
    
}
