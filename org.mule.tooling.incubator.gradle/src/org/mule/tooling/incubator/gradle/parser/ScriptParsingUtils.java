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
	
	public static final char QUOTE_ESCAPE = '\\';
	
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
	    
	    //of the line we want only up to where the cursor is located.
        String entireLine = document.get(region.getOffset(), offset - region.getOffset());
	    
	    //we want to make sure we're not parsing a comment.
	    entireLine = removeLineCommentFromLine(entireLine);
	    
	    return parseDSLLine(entireLine);
	}
	
	public static DSLMethodAndMap parseDSLLine(String entireLine) {
	       //remove any leading or trailing spaces.
        entireLine = entireLine.trim();
        
        int argumentStarting = locateMethodArgumentStarting(entireLine);
        
        if (argumentStarting == -2) {
            return null;
        }
        
        String methodName = entireLine;
        
        if (argumentStarting > 0) {
            methodName = entireLine.substring(0, argumentStarting);
        } else {
            argumentStarting = methodName.length();
        }
        
        methodName = removeQuotesIfNecessary(methodName);
        
        if (StringUtils.isEmpty(methodName)) {
            return null;
        }
        
        HashMap<String, String> arguments = null;
        if (argumentStarting < entireLine.length()) {
            arguments = parseGroovyMap(entireLine.substring(argumentStarting));
        } else {
            arguments = new HashMap<String, String>();
        }
        
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
	 * @param line
	 * @return
	 */
	public static int locateMethodArgumentStarting(String line) {
	    
	    boolean isQuote = false;
	    boolean previousIsQuoteEscape = false;
	    
	    for(int i = 0; i < line.length(); i++) {
	        
	        char c = line.charAt(i);
	        
            if (ArrayUtils.contains(QUOTE_CHARS, c)) {
                if (!previousIsQuoteEscape) {
                    isQuote = !isQuote;
                    continue;
                }
            }
	        
	        if (isQuote) {
	            
	            if (c == QUOTE_ESCAPE) {
	                previousIsQuoteEscape = true;
	                continue;
	            }
	        } else {
	            
	            if (Character.isJavaIdentifierPart(c)) {
	                continue;
	            }
	            
	            if (c == ' ' || c == '(') {
	                return i;
	            }
	            
	            //at this point is not a java identifier
	            //and it is not a space or a ( and we're not 
	            //inside a string, this is not valid!
	            return -2;
	        }
	        
	        if (previousIsQuoteEscape) {
	            previousIsQuoteEscape = false;
	        }
	        
	    }
	    
	    return -1;
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
