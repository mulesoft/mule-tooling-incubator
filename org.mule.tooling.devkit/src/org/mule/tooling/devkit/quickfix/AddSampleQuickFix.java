package org.mule.tooling.devkit.quickfix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.mule.tooling.devkit.DevkitImages;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.treeview.ModuleVisitor;
import org.mule.tooling.devkit.treeview.model.Module;
import org.mule.tooling.devkit.treeview.model.ModuleMethod;
import org.mule.tooling.devkit.treeview.model.ModuleUtils;

public class AddSampleQuickFix extends QuickFix {

	public AddSampleQuickFix(String label, ConditionMarkerEvaluator evaluator) {
		super(label, evaluator);
	}

	@Override
	protected ASTRewrite getFix(CompilationUnit unit, Integer errorMarkerStart) {
		ASTRewrite rewrite = null;
		LocateFieldOrMethodVisitor visitor = new LocateFieldOrMethodVisitor(
				errorMarkerStart);

		unit.accept(visitor);
		ModuleVisitor modelVisitor = new ModuleVisitor();
		unit.accept(modelVisitor);
		if (visitor.getNode() != null) {
			goToSampleInDocSampleFile(compilationUnit, visitor.getNode(),
					modelVisitor.getRoot().getModules().get(0));
			// Return an empty rewrite statement so that the error marker is
			// removed since we add the sample.
			AST ast = unit.getAST();
			rewrite = ASTRewrite.create(ast);
		}
		return rewrite;
	}

	@Override
	public Image getImage() {
		return DevkitImages.getManagedImage("", "processor.gif");
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	private void goToSampleInDocSampleFile(ICompilationUnit unit, ASTNode node,
			Module module) {
		IFile file = null;
		IResource resourceFile = null;
		IFolder folder = unit.getJavaProject().getProject()
				.getFolder(DevkitUtils.DOCS_FOLDER);
		ModuleMethod method = null;
		String methodName = ((MethodDeclaration) node).getName().toString();
		for (ModuleMethod moduleMethod : module.getProcessor()) {
			if (moduleMethod.getMethod().getName().toString()
					.equals(methodName)) {
				method = moduleMethod;
			}
		}
		try {
			for (IResource resource : folder.members()) {
				if (resource.getName().matches(".*.sample")) {
					file = unit.getJavaProject().getProject()
							.getFile(resource.getProjectRelativePath());
					resourceFile = resource;
					break;
				}
			}
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		if (file == null)
			return;
		InputStreamReader isr = null;
		try {
			String processorName = ModuleUtils.getMethodName(method);
			Javadoc doc = method.getMethod().getJavadoc();
			String javadoc = doc.toString();
			int startPos = javadoc.indexOf("{@sample.xml");
			int endPos = javadoc.indexOf('}', startPos);
			if(startPos==-1 || endPos==-1)
			    return;
			String sample = javadoc.substring(startPos, endPos).split(" ")[2];
			Writer writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file.getRawLocation().toFile(), true),
					"UTF-8"));

			writer.append(String.format("\n<!-- BEGIN_INCLUDE(%2$s) -->\n"
					+ "\t<%1$s:%3$s/>\n" + "<!-- END_INCLUDE(%2$s) -->\n",
					ModuleUtils.getTargetNameSpace(module), sample,
					processorName));
			writer.close();
			folder.refreshLocal(IResource.DEPTH_ONE, null);
			file = unit.getJavaProject().getProject()
					.getFile(resourceFile.getProjectRelativePath());
			isr = new InputStreamReader(file.getContents());
			BufferedReader ir = new BufferedReader(isr);
			int lineNumber = 0;

			while ((ir.readLine()) != null) {
				lineNumber++;

			}
			// We inserted 3 lines so we are sure that the example starts two
			// lines before the file ends.
			lineNumber -= 2;
			HashMap map = new HashMap();
			map.put(IMarker.LINE_NUMBER, new Integer(lineNumber));
			map.put(IWorkbenchPage.EDITOR_ID_ATTR,
					"org.mule.tooling.devkit.sample.editor.XMLEditor");
			IMarker marker;

			marker = file.createMarker(IMarker.TEXT);

			marker.setAttributes(map);
			// page.openEditor(marker); //2.1 API
			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			// page.openEditor(new SampleDocEditorInput(file, module),
			// "org.mule.tooling.devkit.sample.editor.XMLEditor");
			IDE.openEditor(page, marker); // 3.0 API
			marker.delete();

		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String getDescription() {
		return "This will add a sample in the sample file with a basic structure.";
	}
}
