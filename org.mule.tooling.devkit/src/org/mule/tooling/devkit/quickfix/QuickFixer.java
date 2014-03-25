package org.mule.tooling.devkit.quickfix;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.mule.devkit.generation.api.gatherer.DevkitNotification;
import org.mule.devkit.generation.api.gatherer.Message;

public class QuickFixer implements IMarkerResolutionGenerator {

	private List<DevkitQuickFix> fixes;

	public QuickFixer() {
		loadQuickFixes();
	}

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
		List<IMarkerResolution> proposedFixes = new ArrayList<IMarkerResolution>();
		for (DevkitQuickFix fix : fixes) {
			if (fix.hasFixForMarker(mk)) {
				proposedFixes.add((IMarkerResolution) fix);
			}
		}
		return proposedFixes
				.toArray(new IMarkerResolution[proposedFixes.size()]);
	}

	private boolean isDevkitError(IMarker mk) throws CoreException {
		return "org.eclipse.jdt.apt.pluggable.core.compileProblem".equals(mk
				.getType());
	}

	private void loadQuickFixes() {
		fixes = new ArrayList<DevkitQuickFix>();
		List<DevkitNotification> notifications = new ArrayList<DevkitNotification>();
		notifications.add(Message.DEVKIT_083);
		notifications.add(Message.DEVKIT_212);
		notifications.add(Message.DEVKIT_214);
		notifications.add(Message.DEVKIT_215);
		MessageEquals equals = new MessageEquals(notifications);
		fixes.add(new RemoveAnnotation("Remove @Optional annotation.",
				"Optional", new MessageContains("@Default implies @Optional")));
		fixes.add(new AddSampleQuickFix("Add sample for operation.", equals));
		fixes.add(new AddDatasenseMethodQuickFix("Add datasense methods.",
				new MessageContains("Connector is no DataSense enabled")));
		fixes.add(new RemoveMethodQuickFix("Remove element.", equals));

		notifications = new ArrayList<DevkitNotification>();
		notifications.add(Message.DEVKIT_116);
		notifications.add(Message.DEVKIT_117);
		notifications.add(Message.DEVKIT_118);
		fixes.add(new RemoveAnnotation("Remove @Source annotation.", "Source",
				new MessageEquals(notifications)));
		notifications = new ArrayList<DevkitNotification>();
		notifications.add(Message.DEVKIT_005);
		notifications.add(Message.DEVKIT_094);
		notifications.add(Message.DEVKIT_116);
		fixes.add(new RemoveModifier("Remove static modifier.",
				ModifierKeyword.STATIC_KEYWORD,
				new MessageEquals(notifications)));
		notifications = new ArrayList<DevkitNotification>();
		notifications.add(Message.DEVKIT_004);
		notifications.add(Message.DEVKIT_116);
		fixes.add(new RemoveModifier("Remove final modifier.",
				ModifierKeyword.FINAL_KEYWORD, new MessageEquals(notifications)));

		notifications = new ArrayList<DevkitNotification>();
		notifications.add(Message.DEVKIT_004);
		notifications.add(Message.DEVKIT_005);
		notifications.add(Message.DEVKIT_006);
		fixes.add(new RemoveAnnotation("Remove @Configurable annotation.",
				"Configurable", new MessageEquals(notifications)));

		fixes.add(new AddParamSourceCallbackQuickFix(
				"Add SourceCallback parameter.", new MessageEquals(
						Message.DEVKIT_120)));
		fixes.add(new AddJavadocSampleReferenceQuickFix(
				"Add sample reference.", new MessageMatches(
						Message.DEVKIT_212)));
		fixes.add(new AddJavadocForElementQuickFix(
				"Add Javadoc for param.", new MessageMatches(
						Message.DEVKIT_208)));

	}
}