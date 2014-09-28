package org.mule.tooling.incubator.gradle.editors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

public class SimpleCharBalancingAutoEditStrategy implements IAutoEditStrategy {
	
	private static final String[] OPEN_STRINGS = {"{", "\"", "'"};
	private static final char[] CLOSE_CHARS = {'}', '"', '\''};
	
	
	@Override
	public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		
		if (StringUtils.isEmpty(command.text)) {
			//do nothing
			return;
		}
		
		
		for (int i = 0; i < OPEN_STRINGS.length; i++) {
		
			if (command.text.endsWith(OPEN_STRINGS[i]) && shouldAddClosingBrace(document,command, CLOSE_CHARS[i])) {
				command.text = command.text + CLOSE_CHARS[i];
				command.shiftsCaret = false;
				command.caretOffset = command.offset + 1;
			}
			
		}
		
		
		
		
	}

	private boolean shouldAddClosingBrace(IDocument document, DocumentCommand command, char searchChar) {
		
		int offset = command.offset;
		
		try {
			
			int j = offset;
			IRegion region = document.getLineInformationOfOffset(offset);
			
			while(j <= region.getOffset() + region.getLength()) {
				char current = document.getChar(j++);
				
				//here we should check that the braces are balanced
				//but since this is simple, we only care there is a matching
				//just immediately
				if (current == searchChar) {
					return false;
				}
			}
			
		} catch (BadLocationException ex) {
			//meaning we reached the end of the document
			return true;
		}
		
		return true;
	}

}
