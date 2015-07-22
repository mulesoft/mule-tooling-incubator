package org.mule.tooling.incubator.gradle.content;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.mule.tooling.incubator.gradle.parser.GradleMulePlugin;

public class ApplySyntaxDescriberVisitor extends CodeVisitorSupport {
    private static final String APPLY_METHOD_NAME = "apply";
    
    private static final String PROJECT_CONTEXT_NAME = "project";
    
    private static final String PLUGIN_KEY_NAME = "plugin";
    
    private static final GradleMulePlugin STUDIO = GradleMulePlugin.STUDIO;
    
    private String lastPropertyAccess;
    private boolean isApplyContext;
    
    private boolean foundplugin;
    
    
    
    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        
        if (!APPLY_METHOD_NAME.equals(call.getMethodAsString())) {
            return;
        }
        
        //we might be in some myprop.apply situation which we don't want
        //so we try to ensure that at least the last property access was called
        //project, this is also not very reliable but is better than nothing.
        
        if (lastPropertyAccess != null && !PROJECT_CONTEXT_NAME.equals(lastPropertyAccess)) {
            return;
        }
        
        
        isApplyContext = true;
        
        super.visitMethodCallExpression(call);
        
        //finished apply context
        isApplyContext = false;
    }
    
    @Override
    public void visitMapExpression(MapExpression expression) {
        
        if (!isApplyContext) {
            return;
        }
        
        super.visitMapExpression(expression);
    }

    @Override
    public void visitMapEntryExpression(MapEntryExpression expression) {
        //if we are inside apply context
        if (PLUGIN_KEY_NAME.equals(expression.getKeyExpression().getText())) {
            String value = expression.getValueExpression().getText();
            
            foundplugin = checkPlugin(value, STUDIO) || checkPlugin(value, GradleMulePlugin.STUDIO_LEGACY) || checkPlugin(value, GradleMulePlugin.STUDIO_DOMAIN);                
        }
    }
    
    @Override
    public void visitPropertyExpression(PropertyExpression expression) {
        
        if (!PROJECT_CONTEXT_NAME.equals(expression.getPropertyAsString())) {
            //nothing interesting.
            return;
        }
        
        lastPropertyAccess = PROJECT_CONTEXT_NAME;
        super.visitPropertyExpression(expression);
        lastPropertyAccess = null;
    }

    
    /**
     * Return the truth about wether we found the plugin or not. Even though
     * this might not be the ultimate source of truth given we're not applying 
     * this script to a real gradle project.
     * @return
     */
    public boolean isFoundplugin() {
        return foundplugin;
    }
    
    protected static boolean checkPlugin(String value, GradleMulePlugin matches) {    	
    	return StringUtils.equals(value, matches.getPluginAlias()) || StringUtils.equals(value, matches.getPluginClassName());    	
    }
}
