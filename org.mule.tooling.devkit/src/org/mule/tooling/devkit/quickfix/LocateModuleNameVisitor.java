package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;

public class LocateModuleNameVisitor extends ASTVisitor {

    private String value = "";
    private final String memberName;
    private int location;

    public LocateModuleNameVisitor() {
        memberName = "name";
    }

    public LocateModuleNameVisitor(String memberName) {
        this.memberName = memberName;
    }

    public boolean visit(NormalAnnotation node) {
        return node.getTypeName().toString().equals("Connector") || node.getTypeName().toString().equals("org.mule.api.annotations.Connector")
                || node.getTypeName().toString().equals("Module");
    }

    public boolean visit(MemberValuePair node) {
        if (memberName.equals(node.getName().toString())) {
            setValue(((StringLiteral) node.getValue()).getLiteralValue());
            setLocation(node.getValue().getStartPosition());
        }
        return false;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }
}
