package org.mule.tooling.incubator.gradle.editors.completion;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
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
	
	
	public GradleScriptAutocompleteAnalyzer(IDocument gradleScript, String completionWord, int position) {
		this.gradleScript = gradleScript;
		this.insertPosition = position;
		this.completionWord = completionWord;
		this.docSearch = new FindReplaceDocumentAdapter(gradleScript);
	}
	
	
	/**
	 * TODO - this method is currently very dummy, we would need to improve this.
	 * @return
	 */
	public List<String> buildSuggestions() {
		try {
		    return doBuildSuggestions();
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
		return Collections.emptyList();
	}
	
	
	private List<String> doBuildSuggestions() throws Exception {
	    
	    boolean isInComponentsContext = ScriptParsingUtils.isPositionInClosureContext(gradleScript, MuleGradleProjectCompletionMetadata.COMPONENTS_CLOSURE_SCOPE, insertPosition);
	    
	    
	    //this is horrible but at the moment might work.
        if (docSearch.find(insertPosition, "apply plugin: 'mule", false, true, false, false) == null) {
            return Collections.emptyList();
        }
        
        LinkedList<String> ret = new LinkedList<String>();
        String line = getLineOfPosition();
        
        //if we are on a blank line (or with only comments)
        if (StringUtils.isBlank(line)) {
            //we can suggest dsl options.
            
            ret.add(MuleGradleProjectCompletionMetadata.MULE_PLUGIN_EXTENSION_NAME);
            
            if (isInComponentsContext) {
                ret.addAll(MuleGradleProjectCompletionMetadata.COMPONENTS_SCOPE_DSL_METHODS);
                ret.addAll(MuleGradleProjectCompletionMetadata.COMPONENTS_SCOPE_DSL_COLLECTIONS);
            }
            
            //we don't bother in analyzing other things
            return ret;
        }
        
        //if we are in the components context we might want to check
        //if we can auto complete a map
        if (isInComponentsContext && lineIsDslMethodCall(line, MuleGradleProjectCompletionMetadata.COMPONENTS_SCOPE_DSL_METHODS)) {
            ret.addAll(MuleGradleProjectCompletionMetadata.COMPONENTS_BASIC_DSL);
            
            if (line.startsWith(MuleGradleProjectCompletionMetadata.COMPONENT_PLUGIN_DSL_METHOD)) {
                ret.add(MuleGradleProjectCompletionMetadata.DEPENDENCY_GROUP);
            }
            
            return ret;
        }
        
        String lastWord = completionWord;
        
        if (completionWord.trim().length() == 0) {
            //this might be a proprty accessor
            lastWord = parseLeftSide();
        }
        
        if (lastWord.equals(MuleGradleProjectCompletionMetadata.MULE_PLUGIN_EXTENSION_NAME)) {
            return MuleGradleProjectCompletionMetadata.MULE_PLUGIN_EXTENSION_PROPERTIES;
        }
        
        if (MuleGradleProjectCompletionMetadata.MULE_PLUGIN_EXTENSION_NAME.startsWith(lastWord)) {
            return Arrays.asList(MuleGradleProjectCompletionMetadata.MULE_PLUGIN_EXTENSION_NAME);
        }
        
        return Collections.emptyList();
	}

	private boolean lineIsDslMethodCall(String line, List<String> dslWords) {
        
	    for(String word : dslWords) {
	        if (line.startsWith(word)) {
	            return true;
	        }
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
        String line = gradleScript.get(lineRegion.getOffset(), lineRegion.getLength());
        
        //comments are annoying.
        line = ScriptParsingUtils.removeLineCommentFromLine(line);
        return line.trim();
	}
	
}
