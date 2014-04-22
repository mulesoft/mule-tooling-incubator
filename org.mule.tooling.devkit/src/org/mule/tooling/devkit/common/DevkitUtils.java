package org.mule.tooling.devkit.common;

public class DevkitUtils {

    public static final String ICONS_FOLDER = "icons";
    public static final String DOCS_FOLDER = "doc";
    public static final String DEMO_FOLDER = "demo";
    public static final String TEST_JAVA_FOLDER = "src/test/java";
    public static final String MAIN_MULE_FOLDER = "src/main/app";
    public static final String MAIN_FLOWS_FOLDER = "flows";
    public static final String TEST_RESOURCES_FOLDER = "src/test/resources";
    public static final String GENERATED_SOURCES_FOLDER = "/target/generated-sources/mule";
    public static final String MAIN_RESOURCES_FOLDER = "src/main/resources";
    public static final String MAIN_JAVA_FOLDER = "src/main/java";
    public static final String POM_FILENAME = "pom.xml";
    public static final String POM_TEMPLATE_PATH = "/templates/pom.xml.tmpl";
    public static final String UPDATE_SITE_FOLDER = "/target/update-site/";

    public static final String DEVKIT_3_4_0 = "3.4.0";
    public static final String DEVKIT_3_4_1 = "3.4.1";
    public static final String DEVKIT_3_4_2 = "3.4.2";
    public static final String DEVKIT_3_5_0 = "3.5.0-cascade";
    
    public static final String devkitVersions[] = { DEVKIT_3_4_0, DEVKIT_3_4_1, DEVKIT_3_4_2, DEVKIT_3_5_0 };
    public static final String CATEGORY_COMMUNITY = "Community";
    public static final String CATEGORY_STANDARD = "Standard";
    public static final String connectorCategories[] = { CATEGORY_COMMUNITY, CATEGORY_STANDARD  };
    
    public static String createModuleNameFrom(String name) {
        return name + "Module";
    }

    public static String createConnectorNameFrom(String name) {
        return name + "Connector";
    }

}
