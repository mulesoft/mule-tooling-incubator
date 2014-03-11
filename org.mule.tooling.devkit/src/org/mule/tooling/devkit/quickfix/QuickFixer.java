package org.mule.tooling.devkit.quickfix;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

public class QuickFixer implements IMarkerResolutionGenerator {
	public IMarkerResolution[] getResolutions(IMarker mk) {
		try {
			if (isDevkitError(mk)) {

				return getQuickFixForMarker(mk);
			}
		} catch (CoreException e) {
			return new IMarkerResolution[0];
		}
		return new IMarkerResolution[0];
	}

	private IMarkerResolution[] getQuickFixForMarker(IMarker mk)
			throws CoreException {
		String problem = (String) mk.getAttribute(IMarker.MESSAGE);
		if (problem.contains("@Optional")) {
			return new IMarkerResolution[] { new QuickFix(
					"Remove @Optional annotation.") };
		} else if( problem.contains("{@sample.xml}")){
			return new IMarkerResolution[] { new AddSampleQuickFix(
					"Add sample for operation.") };
		}else{
			return new IMarkerResolution[] {
					new AddDatasenseMethodQuickFix("Add datasense methods."),
					new RemoveMethodQuickFix("Remove processor") };
		}
	}

	// TODO get devkit error code
	private boolean isDevkitError(IMarker mk) throws CoreException {
		String problem = (String) mk.getAttribute(IMarker.MESSAGE);
		return problem.contains("@Optional")
				|| problem.contains("@MetaDataKeyParam")
				|| problem.contains("{@sample.xml}");
	}
}