package org.mule.tooling.devkit.quickfix;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

public class MessageContains implements ConditionMarkerEvaluator {

    final List<String> texts;

    public MessageContains(List<String> texts) {
        this.texts = texts;
    }

    public MessageContains(String text) {
        this.texts = new ArrayList<String>();
        texts.add(text);
    }

    @Override
    public boolean hasFixForMarker(IMarker marker) {
        String problem = "";
        try {
            problem = (String) marker.getAttribute(IMarker.MESSAGE);
        } catch (CoreException e) {
            e.printStackTrace();
        }
        for (String notification : texts) {
            if (problem.contains(notification)) {
                return true;
            }
        }
        return false;
    }

}
