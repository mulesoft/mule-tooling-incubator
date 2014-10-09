package org.mule.tooling.incubator.gradle.parser.ast;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.mule.tooling.incubator.gradle.parser.GradleMulePlugin;

public class GradleScriptASTVisitor extends CodeVisitorSupport {

    private List<ScriptMap> appliedPlugins = new LinkedList<ScriptMap>();
    
    private HashMap<GradleMulePlugin, ScriptMap> appliedMulePlugins = new HashMap<GradleMulePlugin, ScriptMap>();
    
    private List<ScriptDependency> dependencies = new LinkedList<ScriptDependency>();
    
    private HashMap<String, String> mulePluginProperties = new HashMap<String, String>();
    
    
    /**
     * Since values are get in sucessive invocations of methods in this visitor we need to track state. 
     * This, of course relies on single threading to parse the script. For
     * convencience the states are called the same as the DSL method calls we're interested in tracking.
     * 
     * @author juancavallotti
     * 
     */
    public static enum STATE {
        apply, mule, components, connector, module, plugin,
    }
    
    public static enum KNOWN_OBJECT {
        mule, mmc, cloudhub
    }
    
    private static LinkedList<STATE> currentContextStack = new LinkedList<STATE>();

    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        boolean contextApplied = applyCurrentContext(call.getMethodAsString()); 
        super.visitMethodCallExpression(call);
        currentContextEnded(contextApplied);
    }

    @Override
    public void visitPropertyExpression(PropertyExpression expression) {
        String objectName = expression.getObjectExpression().getText();
        
        if (isKnownObject(objectName)) {
            String value = expression.getProperty().getText();
            mulePluginProperties.put(objectName, value);
        }
        
    }

    @Override
    public void visitMapExpression(MapExpression expression) {

        if (currentContextStack.isEmpty()) {
            // nothig we care of
            return;
        }

        //we build the script map.
        ScriptMap map = new ScriptMap();
        map.setSourceNode(expression);
        for (MapEntryExpression meexp : expression.getMapEntryExpressions()) {
            String key = meexp.getKeyExpression().getText();
            String value = meexp.getValueExpression().getText();
            map.put(key, value);
        }
        
        STATE currentContext = currentContextStack.peek();
        
        switch (currentContext) {
        case apply:
            applyPluginLogic(map);
            currentContext = null;
            break;
        case plugin:
        case connector:
        case module:
            dependencies.add(new ScriptDependency(map));
            break;
        case mule:
        default:
        }

        super.visitMapExpression(expression);
    }

    // utility methods

    private boolean applyCurrentContext(String contextName) {
        try {
            currentContextStack.push(STATE.valueOf(contextName));
            return true;
        } catch (IllegalArgumentException ex) {
            System.out.println("Ignoring irrelevant context call: " + contextName);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }
    
    
    private boolean isKnownObject(String objectName) {
        try {
            return KNOWN_OBJECT.valueOf(objectName) != null;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
    
    private void currentContextEnded(boolean contextApplied) {
        if (contextApplied) {
            currentContextStack.pop();
        }
    }
    
    
    private void applyPluginLogic(ScriptMap map) {
        appliedPlugins.add(map);
        GradleMulePlugin plugin = GradleScriptASTUtils.decodePluginFromMap(map);
        
        if (plugin != null) {
            appliedMulePlugins.put(plugin, map);
        }
    }
    
    
    // accessors for the information

    public List<ScriptMap> getAppliedPlugins() {
        return appliedPlugins;
    }

    
    public List<ScriptDependency> getDependencies() {
        return dependencies;
    }

    
    public HashMap<GradleMulePlugin, ScriptMap> getAppliedMulePlugins() {
        return appliedMulePlugins;
    }
    
    
    public HashMap<String, String> getMulePluginProperties() {
        return mulePluginProperties;
    }

    public boolean hasGradleMulePlugin(GradleMulePlugin plugin) {
        return appliedMulePlugins.containsKey(plugin);
    }
    
}
