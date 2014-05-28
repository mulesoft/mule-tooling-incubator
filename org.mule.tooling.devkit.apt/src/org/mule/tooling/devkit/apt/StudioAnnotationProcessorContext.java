package org.mule.tooling.devkit.apt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

import org.mule.devkit.apt.AnnotationProcessorManifest;
import org.mule.devkit.apt.dependency.SimpleDependency;
import org.mule.devkit.apt.model.factory.FactoryHolder;
import org.mule.devkit.generation.api.Context;
import org.mule.devkit.generation.api.Dependency;
import org.mule.devkit.generation.api.License;
import org.mule.devkit.generation.api.Manifest;
import org.mule.devkit.generation.api.MavenInformation;
import org.mule.devkit.generation.api.Product;
import org.mule.devkit.model.Identifiable;
import org.mule.devkit.model.code.CodeModel;
import org.mule.devkit.model.code.Type;
import org.mule.devkit.model.code.writer.FilerCodeWriter;
import org.mule.devkit.model.schema.SchemaModel;
import org.mule.devkit.model.studio.StudioModel;
import org.mule.tooling.devkit.apt.factory.StudioGenericTypeFactory;
import org.mule.tooling.devkit.apt.factory.StudioPathUtils;

public class StudioAnnotationProcessorContext implements Context {

    private Messager messager;
    private CodeModel codeModel;
    private SchemaModel schemaModel;
    private StudioModel studioModel;
    private AnnotationProcessorManifest manifest;
    private Map<Tuple<Product, Identifiable, String>, Object> classesByRole = new HashMap<Tuple<Product, Identifiable, String>, Object>();
    private MavenInformation mavenInformation;
    public static final String MAVEN_INFORMATION_FILE = "mavenInformationFile";

    public StudioAnnotationProcessorContext(ProcessingEnvironment env) {
        codeModel = new CodeModel(new FilerCodeWriter(env.getFiler()));
        schemaModel = new SchemaModel(new FilerCodeWriter(env.getFiler()));
        studioModel = new StudioModel(new FilerCodeWriter(env.getFiler()));
        manifest = new AnnotationProcessorManifest(this);
        messager = env.getMessager();
        FactoryHolder.setPathUtils(new StudioPathUtils(env));
        FactoryHolder.setGenericTypeFactory(new StudioGenericTypeFactory());
        //TODO: DEVKIT-703 Extreme hack to avoid RestClient dependency checkers 
        mavenInformation = new StudioMavenInformation();
    }

    private class StudioMavenInformation implements MavenInformation {

        @Override
        public String getGroupId() {
            return "dummy";
        }

        @Override
        public String getArtifactId() {
            return "dummy";
        }

        @Override
        public String getVersion() {
            return "dummy";
        }

        @Override
        public License getLicense() {
            return null;
        }

        @Override
        public String getOutputDirectory() {
            return "null";
        }

        @Override
        public String getName() {
            return "dummy";
        }

        @Override
        public String getCategory() {
            return "dummy";
        }

        @Override
        public Set<Dependency> getDependencies() {
            Set<Dependency> dummy = new HashSet<Dependency>();
            dummy.add(SimpleDependency.create("org.mule.transports:mule-transport-http:3.0.0"));
            return dummy;
        }

    }

    @Override
    public CodeModel getCodeModel() {
        return codeModel;
    }

    @Override
    public SchemaModel getSchemaModel() {
        return schemaModel;
    }

    @Override
    public StudioModel getStudioModel() {
        return studioModel;
    }

    @Override
    public Manifest getManifest() {
        return manifest;
    }

    @Override
    public void note(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    @Override
    public void warn(String msg) {
        messager.printMessage(Diagnostic.Kind.WARNING, msg);
    }

    @Override
    public void error(String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg);
    }

    @Override
    public void error(String msg, Identifiable element) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg, element.unwrap());
    }

    @Override
    public void debug(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, "DEBUG: " + msg);
    }

    @Override
    public <T> T getProduct(Product product) {
        return (T) classesByRole.get(new Tuple<Product, Type, String>(product, null, null));
    }

    /**
     * Retrieve a previously generated class that fulfills the specified role
     * 
     * @param product
     *            Role to be fulfilled
     * @return A previously generated class
     */
    @Override
    public <T> T getProduct(Product product, Identifiable identifiable) {
        return (T) classesByRole.get(new Tuple<Product, Identifiable, String>(product, identifiable, null));
    }

    /**
     * Retrieve a previously generated class that fulfills the specified role
     * 
     * @param product
     *            Role to be fulfilled
     * @param identifiable
     *            Module for which this role is fulfilled
     * @param methodName
     *            Method for which this role is fulfilled
     * @return A previously generated class
     */
    @Override
    public <T> T getProduct(Product product, Identifiable identifiable, String methodName) {
        return (T) classesByRole.get(new Tuple<Product, Identifiable, String>(product, identifiable, methodName));
    }

    /**
     * @param product
     * @param clazz
     */
    @Override
    public <T> void registerProduct(Product product, T clazz) {
        this.classesByRole.put(new Tuple<Product, Identifiable, String>(product, null, null), clazz);
    }

    /**
     * @param product
     * @param clazz
     */
    @Override
    public <T> void registerProduct(Product product, Identifiable identifiable, T clazz) {
        this.classesByRole.put(new Tuple<Product, Identifiable, String>(product, identifiable, null), clazz);
    }

    /**
     * @param product
     * @param clazz
     */
    @Override
    public <T> void registerProduct(Product product, Identifiable identifiable, String methodName, T clazz) {
        this.classesByRole.put(new Tuple<Product, Identifiable, String>(product, identifiable, methodName), clazz);
    }

    @Override
    public <T> List<T> getProductList(Product product) {
        List<T> generatedClasses = new ArrayList<T>();
        for (Tuple<Product, Identifiable, String> tuple : this.classesByRole.keySet()) {
            if (tuple.first.equals(product)) {
                generatedClasses.add((T) this.classesByRole.get(tuple));
            }
        }

        return generatedClasses;
    }

    @Override
    public <T> List<T> getModulesByProduct(Product product) {
        List<T> modules = new ArrayList<T>();
        for (Tuple<Product, Identifiable, String> tuple : classesByRole.keySet()) {
            if (tuple.first.equals(product)) {
                modules.add((T) tuple.second);
            }
        }
        return modules;
    }

    @Override
    public MavenInformation getMavenInformation() {
        return mavenInformation;
    }

    private class Tuple<T, U, X> {

        private final T first;
        private final U second;
        private final X third;
        private transient final int hash;

        public Tuple(T f, U s, X x) {
            this.first = f;
            this.second = s;
            this.third = x;
            hash = (first == null ? 0 : first.hashCode() * 31) + (second == null ? 0 : second.hashCode() * 17) + (third == null ? 0 : third.hashCode());
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object oth) {
            if (this == oth) {
                return true;
            }
            if (oth == null || !(getClass().isInstance(oth))) {
                return false;
            }
            Tuple<T, U, X> other = getClass().cast(oth);
            return (first == null ? other.first == null : first.equals(other.first)) && (second == null ? other.second == null : second.equals(other.second))
                    && (third == null ? other.third == null : third.equals(other.third));
        }
    }

    @Override
    public void warn(String msg, Identifiable element) {
        messager.printMessage(Diagnostic.Kind.WARNING, msg, element.unwrap());
    }

    @Override
    public void note(String msg, Identifiable element) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg, element.unwrap());
    }

    @Override
    public Messager getMessager() {
        return messager;
    }
}
