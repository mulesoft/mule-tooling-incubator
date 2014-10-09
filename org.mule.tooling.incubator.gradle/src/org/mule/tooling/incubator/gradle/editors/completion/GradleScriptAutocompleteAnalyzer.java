package org.mule.tooling.incubator.gradle.editors.completion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.mule.tooling.incubator.gradle.editors.completion.model.SimplifiedGradleProject;
import org.mule.tooling.incubator.gradle.parser.DSLMethodAndMap;
import org.mule.tooling.incubator.gradle.parser.GradleMuleBuildModelProvider;
import org.mule.tooling.incubator.gradle.parser.GradleMulePlugin;
import org.mule.tooling.incubator.gradle.parser.ScriptParsingUtils;

/**
 * Model helper class to help computing auto-completion suggestions. This class is in charge of parsing
 * the script and extracting all the information that would be necessary. This is as well a best-effort 
 * approach since we don't have the ability to really introspect objects.
 * @author juancavallotti
 */
public class GradleScriptAutocompleteAnalyzer {
	private static final char PROPERTY_OPERATOR = '.'; 
	
	
	private final IDocument gradleScript;
	private final int insertPosition;
	private final String completionWord;
	private final FindReplaceDocumentAdapter docSearch;
	private final GradleMuleBuildModelProvider modelProvider;
	
	
	public GradleScriptAutocompleteAnalyzer(IDocument gradleScript, String completionWord, int position, GradleMuleBuildModelProvider modelProvider) {
		this.gradleScript = gradleScript;
		this.insertPosition = position;
		this.completionWord = completionWord;
		this.modelProvider = modelProvider;
		this.docSearch = new FindReplaceDocumentAdapter(gradleScript);
	}
	
	
	/**
	 * TODO - this method is currently very dummy, we would need to improve this.
	 * @return
	 */
	public List<GroovyCompletionSuggestion> buildSuggestions() {
		
	    try {
		    return buildCompletionSuggestions();
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
		return Collections.emptyList();
	}
	
	private List<GroovyCompletionSuggestion> buildCompletionSuggestions() throws Exception {
	    
	    
	    //get the current context of auto completion.
	    Class<?> currentContext = buildContextClass(); 
	    DSLMethodAndMap dslMethod = ScriptParsingUtils.parseDSLLine(gradleScript, insertPosition);
        
	    if (lineIsDslMethodCall(dslMethod, currentContext)) {
	        DSLCompletionStrategy strategy = buildDslCompletionStrategy(currentContext);
	        
	        if (strategy == null) {
	            return Collections.emptyList();
	        } else {
	            return strategy.buildSuggestions(dslMethod, currentContext, false);
	        }
	    }
	    
	    if (currentContext != null) {
	        List<GroovyCompletionSuggestion> ret = new LinkedList<GroovyCompletionSuggestion>();
	        ret.addAll(ObjectMetadataCache.buildAndCacheSuggestions(currentContext));
	        
	        if (!currentContext.equals(SimplifiedGradleProject.class)) {
	            return ret;
	        }
	        
	        for (GradleMulePlugin p : GradleMulePlugin.values()) {
	            if (isPluginVisible(p)) {
	                ret.add(new GroovyCompletionSuggestion(GroovyCompletionSuggestionType.PROPERTY, p.getExtensionVariableName(), p.getExtensionClass().getName()));
	            }
	        }
	        return ret;
	    }
	    
	    return Collections.emptyList();
	}
	
	private DSLCompletionStrategy buildDslCompletionStrategy(Class<?> currentContext) {
        
	    if (GradleMulePlugin.STUDIO.getExtensionClass().equals(currentContext)) {
	        return new MuleComponentsDSLCompletionStrategy();
	    }
	    
	    if (GradleMulePlugin.CLOUDHUB.getExtensionClass().equals(currentContext)) {
	        return new CloudhubDSLCompletionStrategy();
	    }
	    
	    if (GradleMulePlugin.MMC.getExtensionClass().equals(currentContext)) {
	        return new MMCDSLCompletionStrategy();
	    }
	    
	    return null;
    }


    private Class<?> buildContextClass() throws Exception {
	    
	    boolean isStudioPluginVisible = isPluginVisible(GradleMulePlugin.STUDIO);
	    boolean isInComponentsContext = ScriptParsingUtils.isPositionInClosureContext(gradleScript, MuleGradleProjectCompletionMetadata.COMPONENTS_CLOSURE_SCOPE, insertPosition);
	    
	    GradleMulePlugin extensionPropertyAccess = getExtensionPropertyAccess();
	    
	    if (!isStudioPluginVisible && extensionPropertyAccess == null) {
	        return SimplifiedGradleProject.class;
	    }
	    
	    //we're accessing a property of an extension, we're not on a dsl call
	    if (extensionPropertyAccess != null) {
	        
	        if (isPluginVisible(extensionPropertyAccess)) {
	            return extensionPropertyAccess.getExtensionClass();
	        } else {
	            return null;
	        }
	    }
	    
	    
	    if (isInComponentsContext) {
	        return GradleMulePlugin.STUDIO.getExtensionClass();
	    }
	    
	    
	    
	    return SimplifiedGradleProject.class;
	}
	

	/**
	 * Assumes the model is not null
	 * @param plugin
	 * @return
	 */
	private boolean isPluginVisible(GradleMulePlugin plugin) throws BadLocationException {
        
	    if (modelProvider == null) {
	        return false;
	    }
	    
	    if (!modelProvider.hasGradleMulePlugin(plugin)) {
	        return false;
	    }
	    
	    int pluginLine = modelProvider.getAppliedMulePlugins().get(plugin).getSourceNode().getLineNumber();
	    
	    int currentLine = gradleScript.getLineOfOffset(insertPosition);
        
	    return pluginLine < currentLine;
    }
	
	
	private GradleMulePlugin getExtensionPropertyAccess() throws Exception {
	    String propertyName = parseLeftSide();
	    
	    if (StringUtils.isEmpty(propertyName)) {
	        return null;
	    }
	    
	    for(GradleMulePlugin p : GradleMulePlugin.values()) {
	        if (StringUtils.equals(propertyName, p.getExtensionVariableName())) {
	            return p;
	        }
	    }
	    
	    return null;
	}
    
    private boolean lineIsDslMethodCall(DSLMethodAndMap method, Class<?> context) throws BadLocationException {
        
        if (method == null) {
            return false;
        }
        
        if (context == null) {
            return false;
        }
        
        if (DslReflectionUtils.contextContainsDSLMethod(method.getMethodName(), context)) {
            return true;
        }
        
        return false;
    }
    
    

    private String parseLeftSide() throws Exception {
		
		if (gradleScript.getChar(insertPosition- 1) != PROPERTY_OPERATOR) {
			return "";
		}
		
		//start parsing the right side
		int i = insertPosition - 1;
		int j = i;
		
		while (j >= 0) {
			char currentChar = gradleScript.getChar(--j);
			if (!Character.isJavaIdentifierStart(currentChar) && !Character.isJavaIdentifierPart(currentChar)) {
				return gradleScript.get(j+1, i - 1 - j);
			}
		}
		
		return "";
	}
	
	/**
	 * The line will be returned without leading or trailing whitespaces.
	 * @return
	 * @throws BadLocationException
	 */
	private String getLineOfPosition() throws BadLocationException  {
	    IRegion lineRegion = gradleScript.getLineInformationOfOffset(insertPosition);
        
	    int length = insertPosition - completionWord.length() - lineRegion.getOffset();
	    
	    //do not go through the trouble.
	    if (length <= 0) {
	        return "";
	    }
	    
	    //we are actually interested until the cursor position without the last typed word
	    String line = gradleScript.get(lineRegion.getOffset(), length);
        
        //comments are annoying.
        line = ScriptParsingUtils.removeLineCommentFromLine(line);
        return line.trim();
	}
	
}
