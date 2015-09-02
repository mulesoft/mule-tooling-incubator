package org.mule.tooling.devkit.templates;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;

public class LocateModuleNameVisitor extends ASTVisitor {

    private String value = "";
    private final String memberName;

    public LocateModuleNameVisitor() {
        memberName = "name";
    }

    public LocateModuleNameVisitor(String memberName) {
        this.memberName = memberName;
    }

    public boolean visit(NormalAnnotation node) {
        return node.getTypeName().toString().equals("Connector") || node.getTypeName().toString().equals("Module");
    }

    public boolean visit(MemberValuePair node) {
        if (memberName.equals(node.getName().toString())) {
            setValue(((StringLiteral) node.getValue()).getLiteralValue());
        }
        return false;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
