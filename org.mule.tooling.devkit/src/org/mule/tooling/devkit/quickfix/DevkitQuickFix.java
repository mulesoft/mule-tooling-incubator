package org.mule.tooling.devkit.quickfix;

import org.eclipse.core.resources.IMarker;

public interface DevkitQuickFix {
	boolean hasFixForMarker(IMarker marker);
}
