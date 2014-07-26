package org.mule.tooling.incubator.maven.core;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptorBuilder;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.xpp3.SettingsXpp3Reader;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

import java.io.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 *
 */
public class MavenArtifactResolver {

    private static MavenArtifactResolver instance;

    MavenArtifactResolver() {
    }

    public static MavenArtifactResolver getInstance() throws Exception {
        if (instance == null) {
            instance = new MavenArtifactResolver();
            instance.initialize();
        }
        return instance;
    }

    private static String basedir;

    private ArtifactRepository localRepository;

    protected File getLocalArtifactPath(Artifact artifact) {
        return new File(localRepository.getBasedir(), localRepository.pathOf(artifact));
    }

    protected void initialize() throws Exception {
        basedir = getBasedir();

        File settingsFile = new File(System.getProperty("user.home"), ".m2/settings.xml");
        String localRepo = null;
        if (settingsFile.exists()) {
            Settings settings = new SettingsXpp3Reader().read(new FileReader(settingsFile));
            localRepo = settings.getLocalRepository();
        } else {
            localRepo = System.getProperty("user.home") + "/.m2/repository";
        }
        if (localRepo == null) {
            localRepo = System.getProperty("user.home") + "/.m2/repository";
        }

        localRepository = new DefaultArtifactRepository("local", "file://" + localRepo, new DefaultRepositoryLayout());

    }

    public static String getBasedir() {
        if (basedir != null) {
            return basedir;
        }

        basedir = System.getProperty("basedir");

        if (basedir == null) {
            basedir = new File("").getAbsolutePath();
        }

        return basedir;
    }

    public Model getModel(File artifactFile) throws IOException {
        Model model = null;
        FileReader reader = null;
        MavenXpp3Reader mavenreader = new MavenXpp3Reader();
        try {
            reader = new FileReader(artifactFile);
            model = mavenreader.read(reader);
        } catch (Exception ex) {

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return model;
    }

    public Model getModel(Artifact artifact) throws IOException {
        File artifactFile = getLocalArtifactPath(artifact);
        return getModel(artifactFile);
    }

    public Model getModel(String groupId, String artifactId, String version, String type) throws IOException {
        DefaultArtifact artifact = new DefaultArtifact(groupId, artifactId, VersionRange.createFromVersion(version), null, type, null, new DefaultArtifactHandler(type));
        return getModel(artifact);
    }

    public PluginDescriptor getPluginDescriptor(File file) throws IOException, PlexusConfigurationException {
        PluginDescriptor descriptor = null;
        if (file.exists()) {
            JarFile jarFile = new JarFile(file);
            ZipEntry entry = jarFile.getEntry("META-INF/maven/plugin.xml");
            InputStream plugin = jarFile.getInputStream(entry);
            BufferedReader in = new BufferedReader(new InputStreamReader(plugin));
            descriptor = new PluginDescriptorBuilder().build(in);
        }
        return descriptor;
    }

    public PluginDescriptor getPluginDescriptor(String path) throws IOException, PlexusConfigurationException {
        return getPluginDescriptor(new File(path));

    }

    public PluginDescriptor getPluginDescriptor(Artifact artifact) throws IOException, PlexusConfigurationException {
        File artifactFile = getLocalArtifactPath(artifact);
        return getPluginDescriptor(artifactFile);
    }

    public PluginDescriptor getPluginDescriptor(String groupId, String artifactId, String version, String type) throws IOException, PlexusConfigurationException {
        DefaultArtifact artifact = new DefaultArtifact(groupId, artifactId, VersionRange.createFromVersion(version), null, type, null, new DefaultArtifactHandler(type));
        return getPluginDescriptor(artifact);
    }
    /**
     * public void test() throws Exception { DefaultArtifact artifact = new DefaultArtifact("org.mule.tools.devkit","mule-devkit-parent","3.4.0",null, "pom", null, new
     * DefaultArtifactHandler("pom")); List<ArtifactRepository> remotes = new ArrayList<ArtifactRepository>(); remotes.add(new DefaultArtifactRepository( "maven-central",
     * "http://repo1.maven.org/maven2/", new DefaultRepositoryLayout()) );
     * 
     * File artifactFile = getLocalArtifactPath(artifact);
     * 
     * Model model = null; FileReader reader = null; MavenXpp3Reader mavenreader = new MavenXpp3Reader(); try { reader = new FileReader(artifactFile); model =
     * mavenreader.read(reader); }catch(Exception ex){} MavenProject project = new MavenProject(model); Dependency dep=model.getDependencies().get(0); String version =
     * model.getProperties().getProperty(dep.getVersion().substring(2,dep.getVersion().length()-1)); DefaultArtifact artifact2 = new
     * DefaultArtifact(dep.getGroupId(),dep.getArtifactId(),version,null, dep.getType(), null, new DefaultArtifactHandler(dep.getType()));
     * 
     * File artifactFile2 = getLocalArtifactPath(artifact2);
     * 
     * String resource = "/Users/pablocabrera/.m2/repository/com/mule/connectors/testdata/connector-testdata-maven-plugin/1.1.4/connector-testdata-maven-plugin-1.1.4.jar"; try {
     * JarFile jarFile = new JarFile(resource); ZipEntry entry = jarFile.getEntry("META-INF/maven/plugin.xml"); InputStream plugin = jarFile.getInputStream(entry); BufferedReader
     * in= new BufferedReader(new InputStreamReader(plugin)); PluginDescriptor descriptor=new PluginDescriptorBuilder().build(in); String
     * goal=((MojoDescriptor)descriptor.getMojos().get(0)).getId(); System.out.println(goal); }catch (Exception ex){
     * 
     * }
     * 
     * }
     */
}
