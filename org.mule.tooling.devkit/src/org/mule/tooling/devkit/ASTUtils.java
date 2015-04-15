package org.mule.tooling.devkit;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.mule.tooling.devkit.treeview.ModuleVisitor;

public class ASTUtils {

    /**
     * Reads a ICompilationUnit and creates the AST DOM for manipulating the Java source file. It uses the AST API that handles JLS3.
     * 
     * @param unit
     *            Java element to be used to generate a compilation unit.
     * 
     * @return Compilation unit to be processed.
     */

    public static CompilationUnit parse(ICompilationUnit unit) {
        return org.eclipse.jdt.ui.SharedASTProvider.getAST(unit, org.eclipse.jdt.ui.SharedASTProvider.WAIT_YES, null);
    }

    public static boolean contains(ASTNode node, int location) {
        if (node == null)
            return false;
        return (node.getStartPosition() <= location) && (location <= (node.getStartPosition() + node.getLength()));
    }
    
    public static boolean hasConnector(IPackageFragment mypackage, ModuleVisitor visitor) throws JavaModelException {

        for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
            // now create the AST for the ICompilationUnits
            CompilationUnit parse = ASTUtils.parse(unit);
            if (parse != null) {
                parse.accept(visitor);
            }
        }
        if (visitor.getRoot().getModules() != null && !visitor.getRoot().getModules().isEmpty()) {
            return true;
        }
        return false;
    }
}
