package org.mule.tooling.incubator.gradle.parser;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

public class ScriptParsingUtils {
	
	private static final Character SCOPE_OPEN = '{';
	private static final char SCOPE_CLOSE = '}';
	
	private static final String ARG_MAP_DELIMITER_TOKENS = "[]:,";
	
	private static final char[] QUOTE_CHARS = {'"', '\''};
	
	public static final String MISSING_VALUE_KEY = "___$$$MISSING_VALUE_FOR_KEY$$$___";
	
	/**
	 * Do a best effort to parse a DSL method call with a hash map. This presents several
	 * challenges, and the implementation will be evolving or completely change in the future.
	 * @param document
	 * @param offset
	 * @return the method call or null if we're unable to parse.
	 */
	public static DSLMethodAndMap parseDSLLine(IDocument document, int offset) throws BadLocationException {
	    
	    //first, get the line
	    IRegion region = document.getLineInformationOfOffset(offset);
	    
	    String entireLine = document.get(region.getOffset(), region.getLength());
	    
	    //we want to make sure we're not parsing a comment.
	    entireLine = removeLineCommentFromLine(entireLine);
	    
	    return parseDSLLine(entireLine);
	}
	
	public static DSLMethodAndMap parseDSLLine(String entireLine) {
	       //remove any leading or trailing spaces.
        entireLine = entireLine.trim();
        
        
        int argumentStarting = locateMethodArgumentStarting(entireLine);
        
        //we need to get the first one
        String methodName = entireLine.substring(0, argumentStarting);
        
        if (StringUtils.isEmpty(methodName)) {
            return null;
        }
        
        if (methodName.contains(" ")) {
            String[] parts = methodName.split(" ");
            methodName = parts[parts.length - 1];
        }
        
        HashMap<String, String> arguments = parseGroovyMap(entireLine.substring(argumentStarting));
        
        return new DSLMethodAndMap(methodName, arguments);
	}
	
	/**
	 * Parse from a string a groovy map.
	 * @param argumentsLine
	 * @return
	 */
	public static HashMap<String, String> parseGroovyMap(String argumentsLine) {
        
	    argumentsLine = argumentsLine.trim();
	    
	    StringTokenizer tokenizer = new StringTokenizer(argumentsLine, ARG_MAP_DELIMITER_TOKENS, false);
	    
	    //the map is in the form of [key1: 'value1', key2: value2, ...]
	    //the square braces are optional.
	    
	    HashMap<String, String> ret = new HashMap<String, String>();
	    //we do a best effort apprach on parsing.
	    int numTokens = tokenizer.countTokens();
	    
	    for(int i = 0; i < numTokens; i++) {
	        String key = tokenizer.nextToken().trim();
	        String value = null;
	        try {
	            value = tokenizer.nextToken().trim();
	            i++;
	            //increase the current toke so we don't eventually run out of tokens.
	        } catch (NoSuchElementException ex) {
	            //means the declaration is incomplete.
	            ret.put(MISSING_VALUE_KEY, key);
	        }
	        ret.put(key, value);
	    }
	    return ret;
    }

    /**
	 * Remove the comments from a line.
	 * @param line
	 * @return
	 */
	public static String removeLineCommentFromLine(String line) {
	    
	    int oneLineComment = line.indexOf("//");
	    
	    if (oneLineComment >= 0) {
	        line = line.substring(0, oneLineComment);
	    }
	    
	    return line;
	}
	
	/**
	 * If we don't locate the starting of the method, then we return the length of the line
	 * this makes easier further processing since the caller will always get a value that it
	 * can use to substring.
	 * 
	 * IMPORTANT NOTE: GROOVY allows method names as strings, this implementation will surely
	 * not detect these in most cases, this needs to be improved.
	 * 
	 * @param line
	 * @return
	 */
	public static int locateMethodArgumentStarting(String line) {
	    
	    //the method name should be up to the first space or opening (.
        int methodParenthesisPos = line.indexOf('(');
        int methodSpacePos = line.indexOf(' ');
        
        if (methodParenthesisPos == -1) {
            methodParenthesisPos = line.length();
        } else {
          //if found parenthesis, then give priority
          methodSpacePos = line.length();
        }
        
        if (methodSpacePos == -1) {
            methodSpacePos = line.length();
        }
        
        return methodSpacePos < methodParenthesisPos ? methodSpacePos : methodParenthesisPos; 
        
	}
	
	/**
	 * Check if the given position is in the context of the given closure. This is done by checking
	 * the braces balance, basically the number of braces when { means + 1 and } means -1 should not
	 * become 0 before reaching the position.
	 * 
	 * @param document
	 * @param closureName
	 * @param position
	 * @return
	 * @throws BadLocationException
	 */
	public static boolean isPositionInClosureContext(IDocument document, String closureName, int position) throws BadLocationException {
	    
	    FindReplaceDocumentAdapter searchAdapter = new FindReplaceDocumentAdapter(document);
	    
	    //we need the first occurrence searching backwards from the position.
	    IRegion region = searchAdapter.find(position, closureName, false, true, true, false);
	    
	    //fail fast
	    if (region == null) {
	        return false;
	    }
	    
	    //start looking at the contents.
	    int currentPosition = region.getOffset() + region.getLength();
	    
	    int balance = 0;
	    
	    
	    while(currentPosition < position) {
	        
	        //get and increase the counter.
	        char currentCharacter = searchAdapter.charAt(currentPosition++);
	        
	        if (currentCharacter == SCOPE_OPEN.charValue()) {
	            balance++;
	        }
	        
	        if (currentCharacter == SCOPE_CLOSE) {
	            balance--;
	            if (balance == 0) {
	                //scope has ended or the script is not correctly formed.
	                return false;
	            }
	        }
	    }
	    return true;
	}
	
	
	public static String removeQuotesIfNecessary(String input) {
	    
	    if (StringUtils.isEmpty(input)) {
	        return input;
	    }
	    
	    
	    int length = input.length();
	    
	    char startChar = input.charAt(0);
	    char lastChar = input.charAt(length - 1);
	    
	    if (startChar == lastChar && ArrayUtils.contains(QUOTE_CHARS, startChar)) {
	        return input.substring(1, length - 1);
	    }
	    
	    return input;
	}
	
}
