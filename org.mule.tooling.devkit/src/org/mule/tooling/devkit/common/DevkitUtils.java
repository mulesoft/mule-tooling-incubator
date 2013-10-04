package org.mule.tooling.devkit.common;

public class DevkitUtils {

    public static final String ICONS_FOLDER = "icons";
    public static final String DOCS_FOLDER = "doc";
    public static final String TEST_JAVA_FOLDER = "src/test/java";
    public static final String MAIN_MULE_FOLDER = "src/main/app";
    public static final String MAIN_FLOWS_FOLDER = "flows";
    public static final String TEST_RESOURCES_FOLDER = "src/test/resources";
    public static final String GENERATED_SOURCES_FOLDER = "/target/generated-sources/mule";
    public static final String MAIN_RESOURCES_FOLDER = "src/main/resources";
    public static final String MAIN_JAVA_FOLDER = "src/main/java";
    public static final String POM_FILENAME = "pom.xml";
    public static final String POM_TEMPLATE_PATH = "/templates/pom.xml.tmpl";

    public static String createModuleNameFrom(String name) {
        return name + "Module";
    }

    public static String createConnectorNameFrom(String name) {
        return name + "Connector";
    }

}
