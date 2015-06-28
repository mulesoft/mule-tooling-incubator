package org.mule.tooling.incubator.gradle.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.mule.tooling.incubator.gradle.content.StudioGradleEnabledContentDescriber;

import com.mulesoft.build.MulePluginExtension;
import com.mulesoft.build.cloudhub.CloudhubPluginExtension;
import com.mulesoft.build.domain.MuleDomainPluginExtension;
import com.mulesoft.build.mmc.MMCPluginExtension;


public enum GradleMulePlugin {
    
    STUDIO_LEGACY("mulestudio", "mule", MulePluginExtension.class),
    STUDIO("com.mulesoft.studio", "mule", MulePluginExtension.class),
    STUDIO_DOMAIN("com.mulesoft.studio-domain", "mule", MuleDomainPluginExtension.class),
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
            InputStream is = StudioGradleEnabledContentDescriber.class.getResourceAsStream("/META-INF/gradle-plugins/"+pluginAlias+".properties");
            if (is != null)
            	props.load(is);
            pluginClassName = props.getProperty("implementation-class", "com.mulesoft.build.studio.StudioPlugin");
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
