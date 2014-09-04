package org.mule.tooling.devkit.quickfix;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.devkit.ASTUtils;

/**
 * This class is used to create a quickfix that can be applied to devkit errors.
 * 
 * Users can extends this class.
 * 
 */
public abstract class QuickFix implements IMarkerResolution2, DevkitQuickFix {
	String label;

	ICompilationUnit compilationUnit;

	final ConditionMarkerEvaluator evaluator;

	QuickFix(String label, ConditionMarkerEvaluator evaluator) {
		this.label = label;
		this.evaluator = evaluator;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {

		try {
			initializeCompilationUnit(marker);

			Integer errorMarkerStart = (Integer) marker
					.getAttribute(IMarker.CHAR_START);

			CompilationUnit astCompilationUnit = ASTUtils
					.parse(compilationUnit);

			fixError(astCompilationUnit, errorMarkerStart, marker);

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void fixError(CompilationUnit unit, Integer errorMarkerStart,
			IMarker marker) throws CoreException {

		ASTRewrite rewrite = getFix(unit, errorMarkerStart);
		if (rewrite != null) {
			syncCodeChanges(compilationUnit, rewrite);
			marker.delete();
		}
	}

	/**
	 * This method creates all the necessary statements required to fix the
	 * error.
	 * 
	 * @param unit
	 *            The compilation unit that has the error
	 * @param errorMarkerStart
	 *            The start character where the error is located
	 * @return The rewrite statement that contains the changes required to fix
	 *         the issue, or null if this quickfix could not be applied.
	 */
	protected ASTRewrite getFix(CompilationUnit unit, Integer errorMarkerStart) {
		LocateAnnotationVisitor visitor = new LocateAnnotationVisitor(
				errorMarkerStart, "Optional",(ICompilationUnit) unit.getJavaElement());

		unit.accept(visitor);

		if (visitor.getNode() != null) {
			ASTRewrite rewrite = ASTRewrite.create(unit.getAST());
			rewrite.remove(visitor.getNode(), null);
			return rewrite;
		}
		return null;
	}

	/**
	 * Applies the rewrite statement and sync the code changes.
	 * 
	 * @param unit
	 *            Compilation unit that will receive the changes
	 * @param rewrite
	 *            Changes to apply
	 * @throws JavaModelException
	 */
	private void syncCodeChanges(ICompilationUnit unit, ASTRewrite rewrite)
			throws JavaModelException {
		unit.applyTextEdit(rewrite.rewriteAST(), null);
		unit.becomeWorkingCopy(null);
		unit.commitWorkingCopy(true, null);
		unit.discardWorkingCopy();
	}

	/**
	 * Add imports if the CompilationUnit doesn't have them already.
	 * 
	 * @param compilationUnit
	 * @param ast
	 * @param rewrite
	 * @param fullyQualifiedName
	 */
	protected void addImportIfRequired(CompilationUnit compilationUnit,
			ASTRewrite rewrite, String fullyQualifiedName) {
		AST ast = compilationUnit.getAST();
		boolean hasConnectorAnnotationImport = false;

		ListRewrite listImports = rewrite.getListRewrite(compilationUnit,
				CompilationUnit.IMPORTS_PROPERTY);

		for (Object obj : compilationUnit.imports()) {
			ImportDeclaration importDec = (ImportDeclaration) obj;
			if (importDec.getName().getFullyQualifiedName()
					.equals(fullyQualifiedName)) {
				hasConnectorAnnotationImport = true;
			}
		}

		ImportDeclaration id = null;

		if (!hasConnectorAnnotationImport) {
			id = ast.newImportDeclaration();
			id.setName(ast.newName(fullyQualifiedName));
			listImports.insertLast(id, null);
		}
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE);
	}

	/**
	 * Checks that this quickfix applies to the given marker
	 */
	@Override
	public boolean hasFixForMarker(IMarker marker) {
		return evaluator.hasFixForMarker(marker);
	}

	/**
	 * Set the ICompilationUnit from the error marker.
	 * 
	 * @param marker
	 *            Error/Warning located in a resource.
	 */
	private void initializeCompilationUnit(IMarker marker) {
		IResource resource = marker.getResource();
		IJavaElement javaElement = JavaCore.create(resource);
		compilationUnit = (ICompilationUnit) javaElement
				.getAdapter(ICompilationUnit.class);
	}

}