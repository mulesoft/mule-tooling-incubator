package org.mule.tooling.devkit.assist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.template.java.CompilationUnitContext;
import org.eclipse.jdt.internal.corext.template.java.CompilationUnitContextType;
import org.eclipse.jdt.internal.corext.template.java.JavaContextType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.correction.QuickTemplateProcessor;
import org.eclipse.jdt.internal.ui.text.template.contentassist.TemplateProposal;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;
import org.eclipse.jdt.ui.text.java.correction.ChangeCorrectionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.TextInvocationContext;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.part.FileEditorInput;
import org.mule.tooling.devkit.DevkitImages;

public class AssistProcessor implements IQuickAssistProcessor {

	@Override
	public boolean hasAssists(IInvocationContext context) throws CoreException {
		return false;
	}

	@SuppressWarnings("restriction")
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
		IJavaElement element = context.getCompilationUnit().getElementAt(
				context.getSelectionOffset());
		final Image processor = DevkitImages.getManagedImage("",
				"processor.gif");
		if (element.getElementType() != IJavaElement.TYPE) {
			if (hasConnector) {
				ICompilationUnit cu = context.getCompilationUnit();
				IDocument document = getDocument(cu);

				// You can generate template dynamically here!
				Template template = new Template("sample",
						"sample description", "no-context",
						"private void ${name}(){\r\n"
								+ "\tSystem.out.println(\"${name}\")\r\n"
								+ "}\r\n", true);
				IRegion region = new Region(context.getSelectionOffset(), 0);
				TemplateContextType contextType2 = new TemplateContextType(
						"test");
				TemplateContext ctx = new DocumentTemplateContext(contextType2,
						document, region.getOffset(), 0);
				proposal.add(new TemplateProposal(template, ctx, region, null));

			}
			return null;
		}
		if (hasConnector) {
			ICompilationUnit cu = context.getCompilationUnit();
			IDocument document = getDocument(cu);

			IRegion region = new Region(context.getSelectionOffset(), 0);
			TemplateContextType contextType2 = new TemplateContextType("test");
			TemplateContext ctx = new DocumentTemplateContext(contextType2,
					document, region.getOffset(), 0);
			// You can generate template dynamically here!
			Template[] templates = JavaPlugin.getDefault().getTemplateStore()
					.getTemplates();
			for (int i = 0; i != templates.length; i++) {
				Template currentTemplate = templates[i];
				if (currentTemplate.getName().contains("configurable")
						|| currentTemplate.getName().contains("processor"))
					proposal.add(new TemplateProposal(currentTemplate, ctx,
							region, processor));
			}

			proposal.add(new ChangeCorrectionProposal("Rename Connector",
					new RenameConnector(context), 4, processor));

		} else {
			proposal.add(new ChangeCorrectionProposal(
					"Add Interface methods to Connector",
					new AddInterfaceMethodsConnector(context), 4, processor));
		}

		return proposal.toArray(new IJavaCompletionProposal[proposal.size()]);
	}

	private IDocument getDocument(ICompilationUnit cu)
			throws JavaModelException {
		IFile file = (IFile) cu.getResource();
		IDocument document = JavaUI.getDocumentProvider().getDocument(
				new FileEditorInput(file));
		if (document == null) {
			return new Document(cu.getSource()); // only used by test cases
		}
		return document;
	}

	public char[] getTriggerCharacters() {
		return null;
	}
}
