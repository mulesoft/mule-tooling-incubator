package org.mule.tooling.devkit.treeview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.mule.tooling.devkit.quickfix.LocateAnnotationVisitor;
import org.mule.tooling.devkit.treeview.model.ModelUtils;
import org.mule.tooling.devkit.treeview.model.Module;
import org.mule.tooling.devkit.treeview.model.ModuleField;
import org.mule.tooling.devkit.treeview.model.ModuleMethod;
import org.mule.tooling.devkit.treeview.model.ModuleSource;
import org.mule.tooling.devkit.treeview.model.ModuleTransformer;
import org.mule.tooling.devkit.treeview.model.ProjectRoot;
import org.mule.tooling.devkit.treeview.model.Property;

public class ModuleVisitor extends ASTVisitor {

    private ProjectRoot root = new ProjectRoot();

    public ProjectRoot getRoot() {
        return root;
    }

    public void setRoot(ProjectRoot root) {
        this.root = root;
    }

    private Module module;
    private CompilationUnit compilationUnit;

    List<TypeDeclaration> connectors = new ArrayList<TypeDeclaration>();
    List<MethodDeclaration> processor = new ArrayList<MethodDeclaration>();
    List<FieldDeclaration> configurable = new ArrayList<FieldDeclaration>();

    @Override
    public boolean visit(CompilationUnit node) {
        compilationUnit = node;
        LocateAnnotationVisitor visitorConnector = new LocateAnnotationVisitor(0, "org.mule.api.annotations.Connector",(ICompilationUnit) node.getJavaElement());
        LocateAnnotationVisitor visitorModule = new LocateAnnotationVisitor(0, "org.mule.api.annotations.Module",(ICompilationUnit) node.getJavaElement());
        node.accept(visitorConnector);
        if (visitorConnector.getNode() != null) {
            module = new Module(root, (ICompilationUnit) compilationUnit.getJavaElement(), node);
            root.getModules().add(module);
            return true;
        }
        node.accept(visitorModule);
        if (visitorModule.getNode() != null) {
            module = new Module(root, (ICompilationUnit) compilationUnit.getJavaElement(), node);
            root.getModules().add(module);
            return true;
        }
        module = null;
        return false;
    }

    @Override
    public boolean visit(NormalAnnotation node) {
        if (node.getParent() instanceof MethodDeclaration) {
            if (ModelUtils.isAnnotationSupported(node.getTypeName().toString())) {
                ModuleMethod method = null;
                if (ModelUtils.isTransformerMethod(node.getTypeName().toString())) {
                    method = new ModuleTransformer(module, (ICompilationUnit) compilationUnit.getJavaElement(), node);
                } else if (ModelUtils.isSourceMethod(node.getTypeName().toString())) {
                    method = new ModuleSource(module, (ICompilationUnit) compilationUnit.getJavaElement(), node);
                } else {
                    method = new ModuleMethod(module, (ICompilationUnit) compilationUnit.getJavaElement(), node);
                }

                method.setConnectionMethod(ModelUtils.isConnectionAnnotation(node.getTypeName().toString()));
                method.setMethod((MethodDeclaration) node.getParent());
                processor.add((MethodDeclaration) node.getParent());
                module.getProcessor().add(method);
                for (Object value : node.values()) {
                    MemberValuePair valuePair = (MemberValuePair) value;
                    Property prop = new Property(method, (ICompilationUnit) compilationUnit.getJavaElement(), node);
                    prop.setName(valuePair.getName().toString());
                    if (valuePair.getValue() instanceof StringLiteral) {
                        prop.setValue(((StringLiteral) valuePair.getValue()).getLiteralValue());
                    } else {
                        prop.setValue(valuePair.getValue().toString());
                    }
                    method.getProperties().add(prop);

                }
            }
            return false;
        }

        if (!ModelUtils.isAnnotationSupported(node.getTypeName().toString())) {
            return false;
        }

        module.setName(((TypeDeclaration) node.getParent()).getName().toString());
        module.setType("@" + node.getTypeName().toString());
        for (Object value : node.values()) {
            MemberValuePair valuePair = (MemberValuePair) value;

            Property prop = new Property(module, (ICompilationUnit) compilationUnit.getJavaElement(), valuePair);
            prop.setName(valuePair.getName().toString());
            if (valuePair.getValue() instanceof StringLiteral) {
                prop.setValue(((StringLiteral) valuePair.getValue()).getLiteralValue());
            } else {
                prop.setValue(valuePair.getValue().toString());
            }
            module.getProperties().add(prop);
        }

        connectors.add((TypeDeclaration) node.getParent());
        return true;
    }

    @Override
    public boolean visit(MarkerAnnotation node) {
        if (node.getTypeName().toString().equals("Configurable")) {
            configurable.add((FieldDeclaration) node.getParent());
            ModuleField field = new ModuleField(module, (ICompilationUnit) compilationUnit.getJavaElement(), node.getParent());
            field.setField((FieldDeclaration) node.getParent());
            try {
                if (module == null)
                    return false;
                module.getConfigurable().add(field);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        } else if (node.getTypeName().toString().equals("MetaDataCategory")) {
            module.setName(((TypeDeclaration) node.getParent()).getName().toString());
            module.setType("@" + node.getTypeName().toString());
        } else if (ModelUtils.isAnnotationSupported(node.getTypeName().toString())) {
            ModuleMethod method = null;
            if (ModelUtils.isTransformerMethod(node.getTypeName().toString())) {
                method = new ModuleTransformer(module, (ICompilationUnit) compilationUnit.getJavaElement(), node.getParent());
            } else if (ModelUtils.isSourceMethod(node.getTypeName().toString())) {
                method = new ModuleSource(module, (ICompilationUnit) compilationUnit.getJavaElement(), node.getParent());
            } else {
                method = new ModuleMethod(module, (ICompilationUnit) compilationUnit.getJavaElement(), node.getParent());
                method.setConnectionMethod(ModelUtils.isConnectionAnnotation(node.getTypeName().toString()));
            }
            method.setMetadataMethod(ModelUtils.isMetadaMethod(node.getTypeName().toString()));
            method.setMethod((MethodDeclaration) node.getParent());
            module.getProcessor().add(method);
        }
        return false;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        List<?> modifiers = (List<?>) node.getStructuralProperty(FieldDeclaration.MODIFIERS2_PROPERTY);
        for (Object type : modifiers) {
            if (type instanceof Annotation) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean visit(AnnotationTypeDeclaration node) {
        System.out.print(node.getName());
        return super.visit(node);

    }

    @Override
    public boolean visit(MethodDeclaration node) {
        List<?> modifiers = (List<?>) node.getStructuralProperty(MethodDeclaration.MODIFIERS2_PROPERTY);
        for (Object type : modifiers) {
            if (type instanceof Annotation) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        List<?> modifiers = (List<?>) node.getStructuralProperty(TypeDeclaration.MODIFIERS2_PROPERTY);
        for (Object type : modifiers) {
            if (type instanceof Annotation) {
                return true;
            }
        }
        return false;
    }

    public List<MethodDeclaration> getMethods() {
        return processor;
    }

    public List<FieldDeclaration> getFields() {
        return configurable;
    }
}