package org.mule.tooling.incubator.gradle.jobs;

import java.io.ByteArrayInputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.module.ExternalContributionMuleModule;
import org.mule.tooling.incubator.gradle.GradlePluginUtils;
import org.mule.tooling.incubator.gradle.parser.GradleMuleBuildModelProvider;
import org.mule.tooling.incubator.gradle.parser.ast.GradleScriptASTParser;
import org.mule.tooling.incubator.gradle.parser.ast.ScriptDependency;
import org.mule.tooling.maven.dependency.ExternalModuleMavenDependency;
import org.mule.tooling.maven.dependency.MavenDependency;


public class RemoveDependencyFromProjectJob extends AbstractDependencyJob {
    
    private static final String JOB_NAME = "Removing dependency from build script...";
    
    public RemoveDependencyFromProjectJob(ExternalContributionMuleModule module, IMuleProject project) {
        super(JOB_NAME, module, project);
    }

    @Override
    protected IStatus runTask(IProgressMonitor monitor, IFile buildScript) throws Exception {
        String scriptContents = IOUtils.toString(buildScript.getContents(), buildScript.getCharset());
        GradleScriptASTParser parser = new GradleScriptASTParser(scriptContents);
        
        GradleMuleBuildModelProvider provider = parser.walkScript();
        
        MavenDependency dep = ExternalModuleMavenDependency.from(module);
        
        StringBuilder builder = new StringBuilder(scriptContents);
        
        boolean updated = false;
        
        for (ScriptDependency sd : provider.getDependencies()) {
            if (GradlePluginUtils.isSameDependency(sd, dep)) {
                builder.delete(sd.getSourceNode().getStart(), sd.getSourceNode().getEnd());
                updated = true;
                break;
            }
        }
        
        if (!updated) {
            return Status.OK_STATUS;
        }
        
        String result = builder.toString();
        buildScript.setContents(new ByteArrayInputStream(result.getBytes()), false, true, monitor);
        return Status.OK_STATUS;
    }
    
}
