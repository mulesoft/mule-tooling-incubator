package org.mule.tooling.devkit.quickfix;

import org.eclipse.core.resources.IMarker;

public interface ConditionMarkerEvaluator {

	boolean hasFixForMarker(IMarker marker);
}
