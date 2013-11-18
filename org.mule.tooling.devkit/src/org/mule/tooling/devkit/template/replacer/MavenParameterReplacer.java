package org.mule.tooling.devkit.template.replacer;

import java.io.Reader;
import java.io.Writer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.mule.tooling.devkit.common.MavenModel;

public class MavenParameterReplacer implements Replacer {

    private MavenModel mavenModel;

    public MavenParameterReplacer(MavenModel mavenModel) {
        this.mavenModel = mavenModel;
    }

    @Override
    public void replace(Reader reader, Writer writer) throws Exception {
        Velocity.init();
        VelocityContext context = new VelocityContext();

        context.put("groupId", mavenModel.getGroupId());
        context.put("artifactId", mavenModel.getArtifactId());

        context.put("version", mavenModel.getVersion());

        boolean evaluate = Velocity.evaluate(context, writer, "velocity pom.xml rendering", reader);

        if (evaluate == false) {
            throw new Exception("Evaluation of the template failed.");
        }

    }

}
