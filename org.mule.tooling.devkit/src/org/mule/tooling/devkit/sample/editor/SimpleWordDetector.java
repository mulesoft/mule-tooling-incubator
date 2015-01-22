package org.mule.tooling.devkit.sample.editor;

import org.eclipse.jface.text.rules.IWordDetector;

public class SimpleWordDetector implements IWordDetector {

    @Override
    public boolean isWordPart(char arg0) {
        return !Character.isWhitespace(arg0);
    }

    @Override
    public boolean isWordStart(char arg0) {
        return !Character.isWhitespace(arg0);
    }

}
