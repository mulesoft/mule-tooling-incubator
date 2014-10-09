package org.mule.tooling.incubator.gradle.parser.ast;

import org.mule.tooling.incubator.gradle.parser.GradleMulePlugin;


public class GradleScriptASTUtils {
    
    public static GradleMulePlugin decodePluginFromMap(ScriptMap map) {
        
        String key = map.get("plugin");
        
        for(GradleMulePlugin plugin : GradleMulePlugin.values()) {
            if (plugin.getPluginAlias().equals(key) || plugin.getPluginClassName().equals(key)) {
                return plugin;
            }
        }
        
        return null;
    }
    
}
