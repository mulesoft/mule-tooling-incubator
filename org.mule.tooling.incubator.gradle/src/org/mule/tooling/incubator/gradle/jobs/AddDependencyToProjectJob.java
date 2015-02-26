package org.mule.tooling.incubator.gradle.jobs;

import java.io.ByteArrayInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.module.ExternalContributionMuleModule;
import org.mule.tooling.incubator.gradle.GradlePluginUtils;
import org.mule.tooling.incubator.gradle.WellBehavedMavenDependency;
import org.mule.tooling.incubator.gradle.parser.GradleMuleBuildModelProvider;
import org.mule.tooling.incubator.gradle.parser.ast.GradleScriptASTParser;
import org.mule.tooling.maven.dependency.ExternalModuleMavenDependency;
import org.mule.tooling.maven.dependency.MavenDependency;

import static com.mulesoft.build.dependency.MuleProjectDependenciesConfigurer.*;

public class AddDependencyToProjectJob extends AbstractDependencyJob {
    
    private static final String JOB_NAME = "Adding dependency to build script...";
    
    public AddDependencyToProjectJob(ExternalContributionMuleModule module, IMuleProject project) {
        super(JOB_NAME, module, project);
    }

    protected IStatus runTask(IProgressMonitor monitor, IFile scriptFile) throws Exception {
        
        MavenDependency dep = new WellBehavedMavenDependency(ExternalModuleMavenDependency.from(module));
        
        String libraryFileName = module.getContributionLibs();
        
        String script = IOUtils.toString(scriptFile.getContents(), scriptFile.getCharset());
        
        GradleScriptASTParser parser = new GradleScriptASTParser(script);
        
        GradleMuleBuildModelProvider modelProvider = parser.walkScript();
        
        if (GradlePluginUtils.modelContainsDependency(modelProvider, dep)) {
            return Status.OK_STATUS;
        }
        
        StringBuilder scriptBuilder = new StringBuilder(script);
        
        boolean isRegularDependency = StringUtils.endsWith(libraryFileName, ".jar");
        
        //TODO - This needs to be improved.
        String dependencyText = buildDependencyText(dep, 
                isRegularDependency, 
                false);
        
        ASTNode nodeToInsert = isRegularDependency ? modelProvider.getDependenciesNode() : modelProvider.getMuleComponentsNode();
        
        
        //meaning this is null, so we would need to add it at some point.
        if (nodeToInsert == null) {
            String componentsString = isRegularDependency ? buildDependenciesString(dependencyText) : buildComponentsString(dependencyText);
            scriptBuilder.append(componentsString);
        } else {
            String indented = buildIndentedDependency(dependencyText);
            int pos = nodeToInsert.getEnd() - 1;
            scriptBuilder.insert(pos, indented);
        }
        
        String result = scriptBuilder.toString();
        
        scriptFile.setContents(new ByteArrayInputStream(result.getBytes()), false, true, monitor);
        
        return Status.OK_STATUS;
    }
    

	private String buildComponentsString(String dependencyText) {
        return "mule.components {" + buildIndentedDependency(dependencyText) + "}\n";
    }

    private String buildDependenciesString(String dependencyText) {
        return "dependencies {" + buildIndentedDependency(dependencyText) + "}\n";
    }
    
    private String buildDependencyText(MavenDependency dep, boolean noExt, boolean noClassifier) {

        StringBuilder builder = new StringBuilder();
        
        //currently no ext will be handled as 'I want a regular runtime dependency'
        if (noExt) {
            builder.append("compile");
        } else {
            builder.append("plugin");
        }
        
        builder.append(" group: '");
        builder.append(dep.getGroupId());
        builder.append("', name: '");
        builder.append(dep.getArtifactId());
        builder.append("', version: '");
        builder.append(dep.getVersion());
        builder.append("'");
        
        
        return builder.toString();
    }
    
    protected String generateBuiltInComponents(MavenDependency dep) {
        
        String setName = null;
        String artifactName = null;
        
        if (StringUtils.equals(getEE_TRANSPORTS_GROUPID().toString(), dep.getGroupId())) {
            setName = "eeTransports";
            artifactName = StringUtils.substring(dep.getArtifactId(), getMULE_TRANSPORTS_PREFIX().toString().length());
        } else if (StringUtils.equals(getEE_MODULES_GROUPID().toString(), dep.getGroupId())) {
            setName = "eeModules";
            artifactName = StringUtils.substring(dep.getArtifactId(), getMULE_MODULES_PREFIX().toString().length());
        } else if (StringUtils.equals(getCOMMUNITY_TRANSPORTS_GROUPID().toString(), dep.getGroupId())) {
            setName = "transports";
            artifactName = StringUtils.substring(dep.getArtifactId(), getMULE_TRANSPORTS_PREFIX().toString().length());
        } else if (StringUtils.equals(getCOMMUNITY_MODULES_GROUPID().toString(), dep.getGroupId())) {
            setName = "modules";
            artifactName = StringUtils.substring(dep.getArtifactId(), getMULE_MODULES_PREFIX().toString().length());
        }
        
        if (setName == null || artifactName == null) {
            return null;
        }
        
        return setName + " += '" + artifactName + "'";
    }

    private String buildIndentedDependency(String dependencyText) {
        return "\n\t" + dependencyText + "\n";
    }
    
}
