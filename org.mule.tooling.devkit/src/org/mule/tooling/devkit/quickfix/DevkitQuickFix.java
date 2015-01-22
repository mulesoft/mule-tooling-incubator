package org.mule.tooling.devkit.quickfix;

import org.eclipse.core.resources.IMarker;

/**
 * Basic interface to determine if the error can be fixed by this marker.
 */
public interface DevkitQuickFix {

    boolean hasFixForMarker(IMarker marker);
}
