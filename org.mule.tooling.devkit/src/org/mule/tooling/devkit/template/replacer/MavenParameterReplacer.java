package org.mule.tooling.devkit.template.replacer;

import java.io.Reader;
import java.io.Writer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.mule.tooling.devkit.common.ConnectorMavenModel;
import org.mule.tooling.devkit.common.DevkitUtils;

public class MavenParameterReplacer implements Replacer {

    private ConnectorMavenModel mavenModel;
    private String runtimeId;
    private String connectorName;
    private boolean isSoapWithCXF;
    private String wsdlFileName;

    public MavenParameterReplacer(ConnectorMavenModel mavenModel, String runtimeId, String connectorName, boolean isSoapWithCXF, String wsdlFileName) {
        this.mavenModel = mavenModel;
        this.runtimeId = runtimeId;
        this.connectorName = connectorName;
        this.isSoapWithCXF = isSoapWithCXF;
        this.wsdlFileName = wsdlFileName;
    }

    @Override
    public void replace(Reader reader, Writer writer) throws Exception {
        Velocity.init();
        VelocityContext context = new VelocityContext();
        context.put("package", mavenModel.getPackage());
        context.put("groupId", mavenModel.getGroupId());
        context.put("artifactId", mavenModel.getArtifactId());

        context.put("version", mavenModel.getVersion());
        context.put("runtimeId", runtimeId);
        context.put("category", mavenModel.getCategory());
        context.put("addGitInformation", mavenModel.isAddGitInformation());
        context.put("gitConnection", mavenModel.getGitConnection());
        context.put("gitDevConnection", mavenModel.getGitDevConnection());
        context.put("gitUrl", mavenModel.getGitUrl());
        context.put("connectorName", connectorName);
        context.put("isSoapWithCXF", isSoapWithCXF);
        context.put("wsdlFileName", wsdlFileName);
        context.put("authenticationType", mavenModel.getAuthenticationType());
        context.put("moduleName", DevkitUtils.toConnectorName(connectorName));
        context.put("project",mavenModel);
        boolean evaluate = Velocity.evaluate(context, writer, "velocity pom.xml rendering", reader);

        if (evaluate == false) {
            throw new Exception("Evaluation of the template failed.");
        }

    }

}
