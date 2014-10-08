package org.mule.tooling.incubator.gradle.parser;

import java.io.IOException;
import java.util.Properties;

import org.mule.tooling.incubator.gradle.content.StudioGradleEnabledContentDescriber;

import com.mulesoft.build.MulePluginExtension;
import com.mulesoft.build.cloudhub.CloudhubPluginExtension;
import com.mulesoft.build.mmc.MMCPluginExtension;


public enum GradleMulePlugin {
    
    STUDIO("mulestudio", "mule", MulePluginExtension.class),
    MMC("mmc", "mmc", MMCPluginExtension.class),
    CLOUDHUB("cloudhub", "cloudhub", CloudhubPluginExtension.class),
    ;
    
    private final String pluginAlias;
    private final String extensionVariableName;
    private final Class<?> extensionClass;
    private String pluginClassName;
    
    
    private GradleMulePlugin(String alias, String varName, Class<?> extensionClass) {
        this.pluginAlias = alias;
        this.extensionVariableName = varName;
        this.extensionClass = extensionClass;
        
        try {
            Properties props = new Properties();
            props.load(StudioGradleEnabledContentDescriber.class.getResourceAsStream("/META-INF/gradle-plugins/"+pluginAlias+".properties"));
            pluginClassName = props.getProperty("implementation-class");
        } catch (IOException ex) {
            pluginClassName = "";
        }
    }


    
    public String getPluginClassName() {
        return pluginClassName;
    }


    
    public void setPluginClassName(String pluginClassName) {
        this.pluginClassName = pluginClassName;
    }


    
    public String getPluginAlias() {
        return pluginAlias;
    }


    
    public String getExtensionVariableName() {
        return extensionVariableName;
    }


    
    public Class<?> getExtensionClass() {
        return extensionClass;
    }
    
}
