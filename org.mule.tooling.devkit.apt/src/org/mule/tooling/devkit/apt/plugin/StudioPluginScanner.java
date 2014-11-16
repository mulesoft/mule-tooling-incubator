package org.mule.tooling.devkit.apt.plugin;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.mule.devkit.CorePlugin;
import org.mule.devkit.generation.api.Plugin;
import org.mule.devkit.generation.api.PluginScanner;
import org.mule.devkit.generation.atgenerated.AtGeneratedPlugin;
import org.mule.devkit.generation.expressionlanguage.ExpressionLanguagePlugin;
import org.mule.devkit.generation.javadoc.JavaDocPlugin;
import org.mule.devkit.oauth.plugin.OAuthClientPlugin;
import org.mule.devkit.generation.rest.RestClientPlugin;
import org.mule.devkit.generation.studio.MuleStudioPlugin;

public class StudioPluginScanner extends PluginScanner{
	public StudioPluginScanner(){
		
	}
	private List<Plugin> plugins;
    /**
     * Gets all the {@link Plugin}s discovered so far.
     * <p/>
     * A plugins are enumerated when this method is called for the first time,
     * by taking the specified ClassLoader into account.
     *
     * @param ucl The user defined class loader
     */
    public List<Plugin> getAllPlugins(ClassLoader ucl) throws MalformedURLException {
       if( plugins==null ){
    	   plugins = new ArrayList<Plugin>();
    	   plugins.add(new CorePlugin());
    	   plugins.add(new OAuthClientPlugin());
    	   plugins.add(new RestClientPlugin());
    	   plugins.add(new ExpressionLanguagePlugin());
    	   plugins.add(new AtGeneratedPlugin());
    	   plugins.add(new JavaDocPlugin());
    	   plugins.add(new MuleStudioPlugin());
       }
       return plugins;
    }
}
