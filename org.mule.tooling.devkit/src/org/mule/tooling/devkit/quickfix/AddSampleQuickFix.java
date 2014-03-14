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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
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

	public AddSampleQuickFix(String label,ConditionMarkerEvaluator evaluator) {
		super(label,evaluator);
	}
	
	@Override
	public boolean hasFixForMarker(IMarker marker) {
		String problem="";
		try {
			problem = (String) marker.getAttribute(IMarker.MESSAGE);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return problem.contains("{@sample.xml}");
	}
	
	protected void createAST(ICompilationUnit unit, Integer charStart)
			throws JavaModelException {
		CompilationUnit parse = parse(unit);
		LocateFieldOrMethodVisitor visitor = new LocateFieldOrMethodVisitor(
				charStart);

		parse.accept(visitor);
		ModuleVisitor modelVisitor = new ModuleVisitor();
		parse.accept(modelVisitor);
		if (visitor.getNode() != null) {
			goToSampleInDocSampleFile(visitor.getNode(), modelVisitor.getRoot()
					.getModules().get(0));
		}
	}

	@Override
	public Image getImage() {
		return DevkitImages.getManagedImage("", "processor.gif");
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	private void goToSampleInDocSampleFile(ASTNode node, Module module) {
		IFile file = null;
		IResource resourceFile = null;
		IFolder folder = getCurrent().getFolder(DevkitUtils.DOCS_FOLDER);
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
					file = getCurrent().getFile(
							resource.getProjectRelativePath());
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
			String javadoc=doc.toString();
			int startPos = javadoc.indexOf("{@sample.xml");
			int endPos = javadoc.indexOf('}',startPos);
			String sample = javadoc.substring(startPos,endPos).split(" ")[2];
			Writer writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file.getRawLocation().toFile(), true),
					"UTF-8"));

			writer.append(String.format("\n<!-- BEGIN_INCLUDE(%2$s) -->\n"
					+ "\t<%1$s:%3$s/>\n" + "<!-- END_INCLUDE(%2$s) -->\n",
					ModuleUtils.getTargetNameSpace(module), sample,
					processorName));
			writer.close();
			folder.refreshLocal(IResource.DEPTH_ONE, null);
			file = getCurrent().getFile(resourceFile.getProjectRelativePath());
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
					"org.mule.tooling.devkit.sample.editor.editors.XMLEditor");
			IMarker marker;

			marker = file.createMarker(IMarker.TEXT);

			marker.setAttributes(map);
			// page.openEditor(marker); //2.1 API
			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
//			page.openEditor(new SampleDocEditorInput(file, module),
//					"org.mule.tooling.devkit.sample.editor.editors.XMLEditor");
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

	private IProject getCurrent() {
		return resource.getProject();
	}
	
	@Override
	public String getDescription() {
		return "This will add a sample in the sample file with a basic structure.";
	}
}
