package org.mule.tooling.incubator.gradle.content;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.mule.tooling.incubator.gradle.parser.GradleMulePlugin;

public class PluginsSyntaxDescriberVisitor extends CodeVisitorSupport {
    
	private static final String PLUGINS_CONTEXT_NAME = "plugins";
    
    private static final String VERSION_METHOD_NAME = "version";
    
    private static final String PLUGIN_METHOD_NAME = "id";
    
    private boolean pluginsContext;
    private boolean versionContext;
    private boolean pluginContext;
    
    private boolean foundPlugin;
    
    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
    	
    	String methodName = call.getMethodAsString();
    	if (!PLUGINS_CONTEXT_NAME.equals(methodName) && 
    			!VERSION_METHOD_NAME.equals(methodName) &&
    			!PLUGIN_METHOD_NAME.equals(methodName)) {
    		//show is over.
    		return;
    	}
    	
    	if (PLUGINS_CONTEXT_NAME.equals(methodName)) {
    		pluginsContext = true;
    		super.visitMethodCallExpression(call);
    		pluginsContext = false;
    		return;
    	}
    	
    	if (VERSION_METHOD_NAME.equals(methodName) && pluginsContext) {
    		versionContext = true;
    		super.visitMethodCallExpression(call);
    		versionContext = false;
    	}
    	
    	if (PLUGIN_METHOD_NAME.equals(methodName) && versionContext) {
    		pluginContext = true;
    		super.visitMethodCallExpression(call);
    		pluginContext = false;
    	}
    	
    }
    
    @Override
    public void visitArgumentlistExpression(ArgumentListExpression ale) {
    	if (pluginContext) {
    		if (ale.getExpressions().size() >= 1) {
    			//the first one should be the plugin id.
    			String pluginId = ale.getExpression(0).getText();
    			
    			this.foundPlugin = checkPlugin(pluginId, GradleMulePlugin.STUDIO) ||
    					checkPlugin(pluginId, GradleMulePlugin.STUDIO_DOMAIN);
    			
    		}
    		
    	}
    	
    	super.visitArgumentlistExpression(ale);
    }

	public boolean isFoundPlugin() {
		return foundPlugin;
	}
	
    protected static boolean checkPlugin(String value, GradleMulePlugin matches) {    	
    	return StringUtils.equals(value, matches.getPluginAlias()) || StringUtils.equals(value, matches.getPluginClassName());    	
    }
}
