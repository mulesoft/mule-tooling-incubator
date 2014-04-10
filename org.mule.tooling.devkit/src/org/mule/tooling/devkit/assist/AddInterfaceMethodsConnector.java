package org.mule.tooling.devkit.assist;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jface.window.Window;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.NullChange;
import org.mule.tooling.devkit.ASTUtils;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.quickfix.LocateModuleNameVisitor;

public class AddInterfaceMethodsConnector extends CompositeChange {

	final IInvocationContext context;
	final ICompilationUnit unit;
	private ICompilationUnit connectorElement;

	public AddInterfaceMethodsConnector(IInvocationContext context) {
		super("Add methods Connector Composite");
		this.context = context;
		unit = context.getCompilationUnit();
	}

	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		SelectMethodsDialog dialog = new SelectMethodsDialog(null, unit);
		dialog.create();
		if (dialog.open() == Window.OK) {
			CompilationUnit connector = getConnectorClass();

			if (connector != null) {
				addMember(connector, context.getCompilationUnit());

				for (MethodDeclaration method : dialog.getSelectedMethods()) {

				}
			}
		}
		return new NullChange();
	}

	private FieldDeclaration addMember(CompilationUnit connector,
			ICompilationUnit compilationUnit) throws JavaModelException,
			IllegalArgumentException {
		CompilationUnit parse = ASTUtils.parse(connectorElement);

		AST ast = parse.getAST();
		ASTRewrite rewrite = ASTRewrite.create(ast);

		// for getting insertion position
		TypeDeclaration typeDecl = (TypeDeclaration) parse.types().get(0);

		VariableDeclarationFragment newFragment = ast
				.newVariableDeclarationFragment();
		newFragment.setName(ast.newSimpleName(compilationUnit.getElementName()
				.replace(".java", "").toLowerCase()));
		FieldDeclaration newFieldDeclaration = ast
				.newFieldDeclaration(newFragment);
		newFieldDeclaration
				.setType(ast.newSimpleType(ast.newName(compilationUnit
						.getElementName().replace(".java", ""))));
		ListRewrite list = rewrite.getListRewrite(typeDecl,
				TypeDeclaration.BODY_DECLARATIONS_PROPERTY);

		list.insertLast(newFieldDeclaration, null);

		connectorElement.applyTextEdit(rewrite.rewriteAST(), null);
		connectorElement.becomeWorkingCopy(null);
		connectorElement.commitWorkingCopy(true, null);
		connectorElement.discardWorkingCopy();
		return null;
	}

	private CompilationUnit getConnectorClass() {
		IProject project = context.getCompilationUnit().getJavaProject()
				.getProject();
		IFolder folder = project.getFolder(DevkitUtils.MAIN_JAVA_FOLDER);

		return locateConnectorInResource(project,
				folder.getProjectRelativePath());
	}

	CompilationUnit locateConnectorInResource(IProject project,
			IPath folderResource) {
		IFolder folder = project.getFolder(folderResource.makeRelative());

		try {
			for (IResource resource : folder.members()) {
				IJavaElement element = (IJavaElement) resource
						.getAdapter(IJavaElement.class);
				if (element != null) {
					switch (element.getElementType()) {
					case IJavaElement.PACKAGE_FRAGMENT_ROOT:
						System.out.println(element);
						break;
					case IJavaElement.PACKAGE_FRAGMENT:
						System.out.println(element);
						return locateConnectorInResource(project, element
								.getPath().makeRelativeTo(folderResource));
					case IJavaElement.COMPILATION_UNIT:
						CompilationUnit connectorClass = ASTUtils
								.parse((ICompilationUnit) element);
						LocateModuleNameVisitor locator = new LocateModuleNameVisitor();
						connectorClass.accept(locator);
						if (!locator.getValue().isEmpty()) {
							connectorElement = (ICompilationUnit) element;
							return connectorClass;
						}
						break;
					default:
						System.out.println(element);
						break;
					}

				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
