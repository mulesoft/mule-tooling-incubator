package org.mule.tooling.devkit.sample.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

public class XMLScanner extends RuleBasedScanner {
	
	public XMLScanner(ColorManager manager) {
		IToken procInstr =
			new Token(
				new TextAttribute(
					manager.getColor(IXMLColorConstants.PROC_INSTR)));

		IToken xmlAttribute =
				new Token(
					new TextAttribute(
						manager.getColor(IXMLColorConstants.XML_ATTRIBUTE)));
		
		IToken sampleDelimiter =
				new Token(
					new TextAttribute(
						manager.getColor(IXMLColorConstants.BEGIN_END_SAMPLE)));
		
		IRule[] rules = new IRule[4];
		//Add rule for processing instructions
		rules[0] = new SingleLineRule("<?", "?>", procInstr);
		
		// Add generic whitespace rule.
		rules[1] = new WhitespaceRule(new XMLWhitespaceDetector());
		
		rules[2] = new SingleLineRule("\"", "\"", xmlAttribute);
		
		rules[3] = new SingleLineRule("<!--", "-->", sampleDelimiter);

		
		setRules(rules);
	}
}
