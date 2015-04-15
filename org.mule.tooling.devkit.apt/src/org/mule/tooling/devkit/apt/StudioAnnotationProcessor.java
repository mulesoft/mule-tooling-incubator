package org.mule.tooling.devkit.apt;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.internal.apt.pluggable.core.dispatch.IdeBuildProcessingEnvImpl;
import org.mule.devkit.apt.AnnotationProcessor;
import org.mule.devkit.generation.api.AnnotationVerificationException;
import org.mule.devkit.generation.api.Generator;
import org.mule.devkit.generation.api.ModuleAnnotationVerifier;
import org.mule.devkit.generation.api.MultiModuleAnnotationVerifier;
import org.mule.devkit.generation.api.PluginScanner;
import org.mule.devkit.model.module.Module;
import org.mule.tooling.devkit.apt.plugin.StudioPluginScanner;

@SuppressWarnings("restriction")
@SupportedAnnotationTypes(value = { "org.mule.api.annotations.Connector", "org.mule.api.annotations.ExpressionLanguage", "org.mule.api.annotations.Module",
        "org.mule.api.annotations.MetaDataCategory", "org.mule.api.annotations.components.*", "org.mule.api.annotations.oauth.OAuth2" })
@SupportedSourceVersion(javax.lang.model.SourceVersion.RELEASE_7)
@SupportedOptions(value = { "enabledStudioPluginPackage", "enableJavaDocValidation", "mavenInformationFile", "enabledVerboseLogging", "enabledCheckVersion" })
public class StudioAnnotationProcessor extends AnnotationProcessor {

    public static final String ENABLED_DEVKIT_CHECK_VERSION = "enabledDevKitCheckVersion";
    private static final String SUPPORTED_VERSION = "3.7.0-M1-SNAPSHOT";

    @Override
    public void init(ProcessingEnvironment env) {
        super.init(env);
    }

    @Override
    protected void createContext() {
        PluginScanner.setInstance(new StudioPluginScanner());
        context = new StudioAnnotationProcessorContext(processingEnv);
    }

    @Override
    protected void initialize() {
        annotationVerifiers = getAnnotationVerifiers();
        postProcessors = getPostProcessors();
        sortedModuleGenerators = new ArrayList<Generator>();
        printer = new StudioPrinterGatherer();
    }

    /**
     * This method process the annotations. It is called automatically by compiler.
     *
     * @param annotations
     *            the annotations processed
     * @param env
     *            the environment
     *
     * @return true if this processor claimed the annotation and we don' want anyone else to process it.
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {

        if(env.getRootElements().isEmpty())
            return true;
        if (env.getRootElements().size() == 1) {
            if (needsToDisplayBuildTooltip(annotations, env)) {
                processingEnv.getMessager().printMessage(Kind.WARNING,
                        "Please, run a FULL or CLEAN build. In order to validate your @Connector, DevKit needs all project files to be compiled.\n",
                        env.getRootElements().iterator().next());
            }
            return true;
        }
        // If the variabe is defined and the value is true we try to check the version.
        String enabledDevKitCheckVersion = processingEnv.getOptions().get(ENABLED_DEVKIT_CHECK_VERSION);
        if (!StringUtils.isBlank(enabledDevKitCheckVersion)) {
            if (Boolean.valueOf(enabledDevKitCheckVersion)) {
                // If the version is not compatible, ignore the APT run.
                if (!isDevKitVersionSupported()) {
                    processingEnv.getMessager().printMessage(Kind.WARNING,
                            "This version of the plugin will only",
                            env.getRootElements().iterator().next());
                    return true;
                }
            } else {
                // Ignore APT
                return true;
            }
        }
        return super.process(annotations, env);
    }

    private boolean isDevKitVersionSupported() {
        boolean supported = false;
        if (processingEnv instanceof IdeBuildProcessingEnvImpl) {

            IdeBuildProcessingEnvImpl ide = (IdeBuildProcessingEnvImpl) processingEnv;
            IFile pom = ide.getJavaProject().getProject().getFile("pom.xml");
            InputStream input = null;
            try {
                MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
                input = pom.getContents();
                Reader pomInputStream = new InputStreamReader(input);
                Model model = mavenXpp3Reader.read(pomInputStream);

                if (model.getParent().getVersion().equals(SUPPORTED_VERSION)) {
                    supported = true;
                }
            } catch (Exception e) {
                Activator.log(e);
            } finally {
                IOUtils.closeQuietly(input);
            }
        }
        return supported;
    }

    /**
     * 
     * Only show the error if the user is building something is not a connector or if the element being built is a @Connector using a component
     * 
     */
    private boolean needsToDisplayBuildTooltip(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        // Since we are building connectors with Connection Strategies this will
        // always return true
        return true;
    }

    /**
     * To prevent classloader issues to stop the verify cicle this method catch all possible errors
     */
    @Override
    protected void doVerify(Module module, ModuleAnnotationVerifier moduleAnnotationVerifier) throws AnnotationVerificationException {
        try {

            super.doVerify(module, moduleAnnotationVerifier);
        } catch (ClassCastException ex) {
            Activator.log(ex);
        } catch (NoClassDefFoundError ex) {
            Activator.log(ex);
        }
    }

    /**
     * To prevent classloader issues to stop the verify cicle this method catch all possible errors
     */
    @Override
    protected void doVerify(List<Module> modules, MultiModuleAnnotationVerifier moduleAnnotationVerifier) throws AnnotationVerificationException {

        try {
            super.doVerify(modules, moduleAnnotationVerifier);
        } catch (ClassCastException ex) {
            Activator.log(ex);
        } catch (NoClassDefFoundError ex) {
            Activator.log(ex);
        }
    }
}
