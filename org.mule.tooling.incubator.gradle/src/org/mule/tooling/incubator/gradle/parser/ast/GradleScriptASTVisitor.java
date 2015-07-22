package org.mule.tooling.incubator.gradle.parser.ast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.mule.tooling.incubator.gradle.parser.GradleMuleBuildModelProvider;
import org.mule.tooling.incubator.gradle.parser.GradleMulePlugin;

public class GradleScriptASTVisitor extends CodeVisitorSupport implements GradleMuleBuildModelProvider {

    private List<ScriptMap> appliedPlugins = new LinkedList<ScriptMap>();
    
    private HashMap<GradleMulePlugin, ScriptMap> appliedMulePlugins = new HashMap<GradleMulePlugin, ScriptMap>();
    
    private List<ScriptDependency> dependencies = new LinkedList<ScriptDependency>();
    
    private HashMap<String, String> mulePluginProperties = new HashMap<String, String>();
    
    private ASTNode muleComponentsNode;
    
    private ASTNode dependenciesNode;
    
    /**
     * Since values are get in sucessive invocations of methods in this visitor we need to track state. 
     * This, of course relies on single threading to parse the script. For
     * convencience the states are called the same as the DSL method calls we're interested in tracking.
     * 
     * @author juancavallotti
     * 
     */
    public static enum STATE {
        apply, mule, components, connector, module, plugin, dependencies, buildscript,
        compile, runtime, providedCompile, providedRuntime, testCompile, testRuntime,
        providedTestCompile, providedTestRuntime, plugins, id, version
    }
    
    public static enum KNOWN_OBJECT {
        mule, mmc, cloudhub
    }
    
    private static LinkedList<STATE> currentContextStack = new LinkedList<STATE>();
    
    private static final LinkedList<STATE> pluginContextStack = new LinkedList<>(Arrays.asList(STATE.id, STATE.version, STATE.plugins));
    
    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        boolean contextApplied = applyCurrentContext(call.getMethodAsString(), call); 
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
        case compile:
        case runtime:
        case providedCompile:
        case providedRuntime:
        case testCompile:
        case testRuntime:
        case providedTestCompile:
        case providedTestRuntime:
            dependencies.add(new ScriptDependency(map));
            break;
        case mule:
        default:
        }

        super.visitMapExpression(expression);
    }

    // utility methods

    private boolean applyCurrentContext(String contextName, ASTNode node) {
        try {
            currentContextStack.push(STATE.valueOf(contextName));
            trackContext(node);
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
    
    //track the current context and save the ast node.
    private void trackContext(ASTNode node) {
        
        Iterator<STATE> stateReverseIterator = currentContextStack.descendingIterator();
        
        STATE lastState = stateReverseIterator.next();
        
        switch (lastState) {
        case components: 
            muleComponentsNode = node;
            break;
        case dependencies:
            if (stateReverseIterator.hasNext()) {
                if (stateReverseIterator.next() == STATE.buildscript) {
                    break;
                }
            }
            dependenciesNode = node;
            break;
        default:
            break;
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

    @Override
    public ASTNode getMuleComponentsNode() {
        return muleComponentsNode;
    }

    @Override
    public ASTNode getDependenciesNode() {
        return dependenciesNode;
    }
    
    
    @Override
    public void visitArgumentlistExpression(final ArgumentListExpression ale) {
    	
    	if (currentContextStack.equals(pluginContextStack) && !ale.getExpressions().isEmpty()) {
    		String id = ale.getExpression(0).getText();
    		GradleMulePlugin plugin = GradleMulePlugin.getByPluginId(id);
    		
    		if (plugin != null) {
    			//to satisfy the contract
    			ScriptMap map = new ScriptMap() {
    				
    				private static final long serialVersionUID = 1L;

					@Override
    				public ASTNode getSourceNode() {
    					return ale;
    				}
    			};
    			appliedMulePlugins.put(plugin, map);    			
    		}
    		
    	}
    	
    	super.visitArgumentlistExpression(ale);
    }
    
}
