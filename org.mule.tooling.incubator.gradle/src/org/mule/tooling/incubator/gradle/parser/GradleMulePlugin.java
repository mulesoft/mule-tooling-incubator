package org.mule.tooling.incubator.gradle.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.mule.tooling.incubator.gradle.content.StudioGradleEnabledContentDescriber;

import com.mulesoft.build.MulePluginExtension;
import com.mulesoft.build.cloudhub.CloudhubPluginExtension;
import com.mulesoft.build.domain.MuleDomainPluginExtension;
import com.mulesoft.build.mmc.MMCPluginExtension;
import com.mulesoft.build.muleagent.MuleAgentPluginExtension;


public enum GradleMulePlugin {
    
    STUDIO_LEGACY("mulestudio", "mule", MulePluginExtension.class),
    STUDIO("com.mulesoft.studio", "mule", MulePluginExtension.class),
    STUDIO_DOMAIN("com.mulesoft.studio-domain", "mule", MuleDomainPluginExtension.class),
    MMC("com.mulesoft.mmc", "mmc", MMCPluginExtension.class),
    MULE_AGENT("com.mulesoft.mule-agent", "muleAgent", MuleAgentPluginExtension.class),
    MMC_LEGACY("mmc", "mmc", MMCPluginExtension.class),
    CLOUDHUB("com.mulesoft.cloudhub", "cloudhub", CloudhubPluginExtension.class),
    CLOUDHUB_LEGACY("cloudhub", "cloudhub", CloudhubPluginExtension.class),
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
    
    public static GradleMulePlugin getByPluginId(String pluginId) {
    	
    	for(GradleMulePlugin p : values()) {
    		if (StringUtils.equals(pluginId, p.getPluginAlias())) {
    			return p;
    		}
    	}
    	
    	return null;
    }
    
}
