package org.mule.tooling.devkit.quickfix;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;
import org.eclipse.jdt.ui.text.java.correction.ChangeCorrectionProposal;
import org.eclipse.ltk.core.refactoring.NullChange;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.devkit.DevkitImages;

public class AssistProcessor implements IQuickAssistProcessor {

	@Override
	public boolean hasAssists(IInvocationContext context) throws CoreException {
		return true;
	}

	@Override
	public IJavaCompletionProposal[] getAssists(IInvocationContext context,
			IProblemLocation[] locations) throws CoreException {
		boolean hasConnector = false;
		List<IJavaCompletionProposal> proposal = new ArrayList<IJavaCompletionProposal>();
		for (IImportDeclaration importElement : context.getCompilationUnit()
				.getImports()) {
			if (importElement.getElementName().startsWith(
					"org.mule.api.annotations")) {
				hasConnector = true;
				break;
			}
		}
		if (hasConnector) {
			final Image configurable = DevkitImages.getManagedImage("",
					"configurable.gif");
			final Image processor = DevkitImages.getManagedImage("",
					"processor.gif");

			// Configurables
			proposal.add(new ChangeCorrectionProposal(
					"Add configurable",
					new NullChange(
							"Add a configurable field.<p>For more details visit:</p><p>http://www.mulesoft.org/documentation/display/current/Defining+Configurable+Connector+Attributes</p>"),
					5, configurable));
			proposal.add(new ChangeCorrectionProposal(
					"Add configurable with default",
					new NullChange(
							"Add a configurable field with a default value.<p> For more details visit:</p><p>http://www.mulesoft.org/documentation/display/current/Defining+Configurable+Connector+Attributes</p>"),
					4, configurable));

			// Processor
			proposal.add(new ChangeCorrectionProposal("Add processor",
					new NullChange("Add a basic processor"), 5, processor));
			proposal.add(new ChangeCorrectionProposal(
					"Add processor with query",
					new NullChange(
							"Add a proccessor that uses @Query annotation.<p>For more details visit:</p><p>http://www.mulesoft.org/documentation/display/current/Implementing+DataSense+Query+Language+Support</p>"),
					3, processor));
			proposal.add(new ChangeCorrectionProposal(
					"Add processor with default payload",
					new NullChange(
							"Add a processor that receives the payload as input by default."),
					3, processor));
			proposal.add(new ChangeCorrectionProposal(
					"Add paginated processor", new NullChange(
							"Add a processor with automatic paging"), 2,
					processor));

			// Datasense
			proposal.add(new ChangeCorrectionProposal(
					"Add metadata",
					new NullChange(
							"Add datasense operation. <p>For more details visit:</p><p>http://www.mulesoft.org/documentation/display/current/Supporting+DataSense+with+Dynamic+Data+Models</p>"),
					4, processor));

		}
		return proposal.toArray(new IJavaCompletionProposal[proposal.size()]);
	}

}
