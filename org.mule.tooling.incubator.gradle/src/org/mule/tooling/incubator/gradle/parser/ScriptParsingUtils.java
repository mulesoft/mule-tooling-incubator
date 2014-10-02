package org.mule.tooling.incubator.gradle.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

public class ScriptParsingUtils {
	
	private static final Character SCOPE_OPEN = '{';
	private static final char SCOPE_CLOSE = '}';
	private static final char LINE_SEPARATOR = ';';
	private static final char COMMENT_START = '/';
	private static final char MULTILINE_COMMENT_START = '*';
	private static final String MULTILINE_COMMENT_END = "*/";
	
	private static final String ARG_MAP_DELIMITER_TOKENS = "[]:,";
	
	/**
	 * Parse the lines inside a given closure starting from the open brace or SPACES before.
	 * @param document
	 * @param start
	 * @return
	 */
	public static List<ScriptLine> parseScopeLines(IDocument document, int start) throws Exception {
		
		FindReplaceDocumentAdapter docAdapter = new FindReplaceDocumentAdapter(document);
		
		LinkedList<Character> scopesStack = new LinkedList<Character>();
		ArrayList<ScriptLine> ret = new ArrayList<ScriptLine>();
		
		int offset = start;
		
		int lineNumber = document.getLineOfOffset(offset);
		IRegion currentLine = document.getLineInformationOfOffset(offset);
		
		
		//locate the first brace
		for (int i = offset; i < currentLine.getOffset() + currentLine.getLength(); i++) {
			char read = document.getChar(i);
			if (Character.isWhitespace(read)) {
				continue;
			}
			
			if (read == SCOPE_OPEN) {
				scopesStack.push(SCOPE_OPEN);
				offset = i + 1;
				break;
			} else {
				throw new IllegalStateException("The script is malformed");
			}
						
		}
		
		//we've got our starting point, now we can parse the region effectively.
		while (!scopesStack.isEmpty()) {
			lineNumber = document.getLineOfOffset(offset);
			currentLine = document.getLineInformationOfOffset(offset);
			
			for(int i = offset; i <= currentLine.getOffset() + currentLine.getLength(); i++) {
				char read = document.getChar(i);
				
				if (read == SCOPE_OPEN) {
					scopesStack.push(SCOPE_OPEN);
				}
				
				if (read == SCOPE_CLOSE) {
					scopesStack.pop();
					
					//this might have been the last one.
					if (scopesStack.isEmpty()) {
						if (offset + 1 < i) {
							ScriptLine line = buildScriptLine(document, lineNumber, offset, i - 1 - offset);
							ret.add(line);
						}
						//we're done!
						break;
					}
				}
				
				//if we have a semicolon, then it might be multiple lines in one.
				if (read == LINE_SEPARATOR) {
					//we need to read a line but not break
					ScriptLine line = buildScriptLine(document, lineNumber, offset, i - 1 - offset);
					ret.add(line);
					offset = i + 1;
					continue;
				}
				
				//if we find a line comment then we can ignore.
				if (read == COMMENT_START) {
					char next = document.getChar(i + 1);
					if (next == COMMENT_START) {
						//in this case we need to read from offset - 1 to i - 1
						ScriptLine line = buildScriptLine(document, lineNumber, offset, i - 1 - offset);
						
						if (line != null) {
						    ret.add(line);
						}
						
						offset = i;
						//continue to the next line
						break;
					}
					
					if (next == MULTILINE_COMMENT_START) {
						//the offset will become the next occurrence of the multiline close + 2
						IRegion newRegion = docAdapter.find(i, MULTILINE_COMMENT_END, true, true, false, false);
						if (newRegion == null) {
							throw new IllegalStateException("Multiline comment not closed");
						}
						offset = newRegion.getOffset() + MULTILINE_COMMENT_END.length();
						break;
					}
				}
			}
			
			//read the line, we will read from lineStart to offset except from a couple of cases
			if (scopesStack.isEmpty()) {
				//we have finished.
				break;
			}
			
			if (offset > currentLine.getOffset() + currentLine.getLength()) {
				//we have read more than we can :)
				continue;
			}
			
			if (offset == currentLine.getOffset() + currentLine.getLength()) {
				//we have read the entire line
				offset++;
				continue;
			}
			
			//we are in good shape to read what is remaining of the line.
			ScriptLine line = buildScriptLine(document, lineNumber, offset, currentLine.getOffset() + currentLine.getLength() - offset);
			if (line != null) {			    
			    ret.add(line);
			}
			offset = currentLine.getOffset() + currentLine.getLength() + 1;
			
		}
		
		return ret;
	}

	private static ScriptLine buildScriptLine(IDocument document, int lineNumber, int offset, int length) throws BadLocationException {
		
		String fragment = document.get(offset, length);
		
		fragment = fragment.trim();
		
		if (StringUtils.isEmpty(fragment)) {
		    return null;
		}
		
		ScriptLine line = new ScriptLine();
		
		//set the contents, but removing any leading or trailing spaces.
		line.setContent(fragment);
		line.setPosition(lineNumber);
		
		return line;
	}
	
	
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
	    
	    //remove any leading or trailing spaces.
	    entireLine = entireLine.trim();
	    
	    
	    int argumentStarting = locateMethodArgumentStarting(entireLine);
	    
	    //we need to get the first one
	    String methodName = entireLine.substring(0, argumentStarting);
	    
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
	    for(int i = 0; i < tokenizer.countTokens(); i++) {
	        String key = tokenizer.nextToken().trim();
	        String value = tokenizer.nextToken().trim();
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
	
}
