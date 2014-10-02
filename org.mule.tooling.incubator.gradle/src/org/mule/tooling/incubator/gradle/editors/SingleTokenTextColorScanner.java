package org.mule.tooling.incubator.gradle.editors;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Color;

public class SingleTokenTextColorScanner extends RuleBasedScanner {
	
	public SingleTokenTextColorScanner(Color color) {
		Token token = new Token(new TextAttribute(color));
		setDefaultReturnToken(token);
	}
	
}
