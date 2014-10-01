package org.mule.tooling.incubator.gradle.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
}
