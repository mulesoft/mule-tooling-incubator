package org.mule.tooling.incubator.gradle.editors;

import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;

public class SimpleIndentBracesOnEnterStrategy extends DefaultIndentLineAutoEditStrategy {
	
	private static final char OPEN_BRACE = '{';
	
	@Override
	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
		
		if (c.length == 0 && c.text != null && TextUtilities.endsWith(d.getLegalLineDelimiters(), c.text) != -1) {
			super.customizeDocumentCommand(d, c);
		} else {
			return;
		}
		
		try {
			//check if previous character was an opening brace
			if (d.getChar(c.offset -1 ) == OPEN_BRACE) {
				//we need to add a tab and a new line
				c.text = c.text + "\t\n";
				c.shiftsCaret = false;
				c.caretOffset = c.offset + c.text.length() - 1;
				super.customizeDocumentCommand(d, c);
			}
		} catch (Exception ex) {
			//do nothing
		}		
	}
}
