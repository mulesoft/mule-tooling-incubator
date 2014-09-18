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
import org.mule.tooling.devkit.treeview.model.ModelUtils;

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
		notifications.add(Message.PROCESSOR_CANNOT_BE_STATIC);
		notifications.add(Message.PROCESSOR_CANNOT_BE_GENERIC);
		notifications.add(Message.PROCESSOR_MUST_BE_PUBLIC);
		notifications.add(Message.PROCESSOR_CANNOT_BE_ABSTRACT_UNLESS_RESTCALL);

		fixes.add(new RemoveAnnotation("Remove @Processor annotation",
				ModelUtils.PROCESSOR_ANNOTATION, new MessageEquals(notifications)));

		notifications = new ArrayList<DevkitNotification>();
		notifications.add(Message.MODULE_CONNECTOR_ASSIGNED_TO_INTERFACE);
		notifications.add(Message.MODULE_CONNECTOR_CANNOT_HAVE_TYPE_PARAMS);

		fixes.add(new RemoveAnnotation("Remove @Module annotation", ModelUtils.MODULE_ANNOTATION,
				new MessageEquals(notifications)));
		fixes.add(new RemoveAnnotation("Remove @Connector annotation",
		        ModelUtils.CONNECTOR_ANNOTATION, new MessageEquals(notifications)));

		fixes.add(new ChangeModifier("Change modifier to public",
				new MessageEquals(Message.MODULE_CONNECTOR_MUST_BE_PUBLIC)));

		notifications = new ArrayList<DevkitNotification>();
		notifications.add(Message.MODULE_CANNOT_HAVE_CONNECT);
		notifications.add(Message.MODULE_CANNOT_HAVE_VALIDATE_CONNECTION);
		notifications.add(Message.MODULE_CANNOT_HAVE_DISCONNECT);
		notifications.add(Message.MODULE_CANNOT_HAVE_CONNECTION_IDENTIFIER);

		fixes.add(new ChangeAnnotationQuickFix(
				"Change annotation from @Module to @Connector",
				new MessageEquals(notifications)));

		fixes.add(new ChangeInvalidateAnnotation(
				"Change annotation from @InvalidateConnectionOn @ReconnectOn",
				new MessageEquals(Message.INVALIDATECONNECTIONON_IS_DEPRECATED)));

		notifications = new ArrayList<DevkitNotification>();
		notifications.add(Message.MODULE_CANNOT_HAVE_CONNECT);
		notifications.add(Message.MODULE_CANNOT_HAVE_VALIDATE_CONNECTION);
		notifications.add(Message.MODULE_CANNOT_HAVE_DISCONNECT);
		notifications.add(Message.MODULE_CANNOT_HAVE_CONNECTION_IDENTIFIER);
		notifications.add(Message.CONNECT_ONE_METHOD_ONLY);
		notifications.add(Message.DISCONNECT_ONE_METHOD_ONLY);
		notifications.add(Message.VALIDATE_CONNECTION_ONE_METHOD_ONLY);
		notifications.add(Message.CONN_IDENTIFIER_ONE_METHOD_ONLY);
		notifications.add(Message.INJECT_CANNOT_INJECT);
		notifications.add(Message.METHOD_ANNOTATED_MORE_THAN_ONE);
		//TODO: Devkit removed this in master, but in 3.5 the error exists. 
		//notifications.add(Message.METADATARETRIEVER_METADATAKEYRETRIEVER_ONLY_ONE);
		notifications.add(Message.PROCESSOR_CANNOT_BE_STATIC);
		notifications.add(Message.PROCESSOR_CANNOT_BE_GENERIC);
		notifications.add(Message.PROCESSOR_MUST_BE_PUBLIC);
		notifications.add(Message.PROCESSOR_CANNOT_BE_ABSTRACT_UNLESS_RESTCALL);
		notifications.add(Message.PROCESSOR_NAME_ALREADY_IN_USE);
		fixes.add(new RemoveMethodQuickFix("Remove item", new MessageMatches(
				notifications)));

		fixes.add(new RemoveExceptions("Use only ConnectionException",
				new MessageMatches(Message.CONNECT_MUST_THROW_CONNECTION_EXCEPTION)));

		notifications = new ArrayList<DevkitNotification>();
		notifications.add(Message.OPTIONAL_REDUNDANT);
		notifications.add(Message.DEFAULT_IMPLIES_OPTIONAL);
		fixes.add(new RemoveAnnotation("Remove @Optional annotation",
		        ModelUtils.OPTIONAL_ANNOTATION, new MessageMatches(notifications)));

		notifications = new ArrayList<DevkitNotification>();
		notifications.add(Message.SAMPLE_PROCESSOR_XML_DOES_NOT_EXIST);
		notifications.add(Message.SAMPLE_FILE_CONTAINING_EXAMPLES_DOES_NOT_EXIST);
		fixes.add(new AddSampleQuickFix("Add sample for operation",
				new MessageMatches(notifications)));

		notifications = new ArrayList<DevkitNotification>();
		notifications.add(Message.METADATARETRIEVER_NOT_DEFINED_FOR_METADATAKEYPARAM);
		notifications.add(Message.METADATARETRIEVER_NOT_DEFINED_FOR_METADATASTATICKEY);
		notifications.add(Message.METADATARETRIEVER_NOT_DEFINED_FOR_QUERY_METHOD);
		fixes.add(new AddDatasenseMethodQuickFix("Add datasense methods",
				new MessageContains("Connector is no DataSense enabled")));

		fixes.add(new RemoveMethodQuickFix("Remove element",
				new MessageMatches(notifications)));

		notifications = new ArrayList<DevkitNotification>();
		notifications.add(Message.SOURCE_CANNOT_BE_STATIC);
		notifications.add(Message.SOURCE_CANNOT_BE_GENERIC);
		notifications.add(Message.SOURCE_MUST_BE_PUBLIC);
		fixes.add(new RemoveAnnotation("Remove @Source annotation", ModelUtils.SOURCE_ANNOTATION,
				new MessageEquals(notifications)));

		notifications = new ArrayList<DevkitNotification>();
		notifications.add(Message.CONFIGURABLE_CANNOT_BE_STATIC);
		notifications.add(Message.CONN_IDENTIFIER_MUST_BE_STATIC);
		notifications.add(Message.ANNOTATED_METHOD_CANNOT_BE_STATIC);
		notifications.add(Message.PROCESSOR_CANNOT_BE_STATIC);
		notifications.add(Message.SOURCE_CANNOT_BE_STATIC);
		fixes.add(new RemoveModifier("Remove static modifier",
				ModifierKeyword.STATIC_KEYWORD, new MessageMatches(
						notifications)));
		notifications = new ArrayList<DevkitNotification>();
		notifications.add(Message.CONFIGURABLE_CANNOT_BE_FINAL);
		fixes.add(new RemoveModifier("Remove final modifier",
				ModifierKeyword.FINAL_KEYWORD, new MessageEquals(notifications)));

		notifications = new ArrayList<DevkitNotification>();
		notifications.add(Message.CONFIGURABLE_CANNOT_BE_FINAL);
		notifications.add(Message.CONFIGURABLE_CANNOT_BE_STATIC);
		notifications.add(Message.CONFIGURABLE_CANNOT_BE_ARRAY);
		fixes.add(new RemoveAnnotation("Remove @Configurable annotation",
				ModelUtils.CONFIGURABLE_ANNOTATION, new MessageEquals(notifications)));

		fixes.add(new AddParamSourceCallbackQuickFix(
				"Add SourceCallback parameter", new MessageEquals(
						Message.SOURCE_METHOD_MISSING_SOURCECALLBACK_PARAM)));

		fixes.add(new AddJavadocSampleReferenceQuickFix("Add sample reference",
				new MessageMatches(Message.METHDO_MISSING_EXAMPLE)));

		notifications = new ArrayList<DevkitNotification>();
		notifications.add(Message.METHOD_PARAMETER_MISSING_JAVADOC);
		notifications.add(Message.METHOD_MISSING_DESCRIPTION);
		fixes.add(new AddJavadocForElementQuickFix("Add Javadoc for param",
				new MessageMatches(notifications)));
		fixes.add(new AddReturnJavadocQuickFix("Add Javadoc for return",
				new MessageMatches(Message.METHOD_MISSING_RETURN_TYPE_DOCUMENTATION)));
		fixes.add(new AddJavadocForExceptionQuickFix(
				"Add Javadoc for exception", new MessageMatches(
						Message.METHOD_MISSING_DOCUMENTATION_FOR_THROWN_EXCEPTIONS)));

		fixes.add(new AddAnnotationQuickFix("Add Default", new MessageMatches(
				Message.CONFIGURABLE_OPTIONAL_CANNOT_BE_PRIMITIVE)));
		fixes.add(new ChangeAnnotationValueQuickFix(
				"Change to first enum value", new MessageMatches(
						Message.DEFAULT_VALUE_INVALID_FOR_ENUM)));
		fixes.add(new ChangeMinMuleVersion("Change minMuleVersion to 3.4",
				new MessageMatches(Message.METADATA_REQUIRES_MIN_MULE_340)));

		notifications = new ArrayList<DevkitNotification>();
		notifications.add(Message.METHOD_PARAMETER_MISSING_JAVADOC);
        notifications.add(Message.METHOD_MISSING_DESCRIPTION);
        notifications.add(Message.METHOD_PARAMETER_MISSING_JAVADOC);
        notifications.add(Message.METHOD_MISSING_RETURN_TYPE_DOCUMENTATION);
        notifications.add(Message.METHOD_MISSING_DOCUMENTATION_FOR_THROWN_EXCEPTIONS);
        notifications.add(Message.SAMPLE_PROCESSOR_XML_DOES_NOT_EXIST);
        notifications.add(Message.SAMPLE_FILE_CONTAINING_EXAMPLES_DOES_NOT_EXIST);
		fixes.add(new DisableJavadocQuickFix(new MessageMatches(notifications)));
	}
}