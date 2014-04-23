package org.mule.tooling.devkit.quickfix;

import org.eclipse.core.resources.IMarker;

/**
 * If you want to create an evaluator for error markers implement this
 * interface. It is used with Devkit Quick Fixes to determine if the error matches
 */
public interface ConditionMarkerEvaluator {

	boolean hasFixForMarker(IMarker marker);
}
