package org.mule.tooling.devkit.treeview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
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
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.mule.tooling.devkit.ASTUtils;
import org.mule.tooling.devkit.DevkitUIPlugin;
import org.mule.tooling.devkit.quickfix.LocateAnnotationVisitor;
import org.mule.tooling.devkit.treeview.model.MetaDataCategory;
import org.mule.tooling.devkit.treeview.model.ModelUtils;
import org.mule.tooling.devkit.treeview.model.Module;
import org.mule.tooling.devkit.treeview.model.ModuleField;
import org.mule.tooling.devkit.treeview.model.ModuleFieldStragegy;
import org.mule.tooling.devkit.treeview.model.ModuleMethod;
import org.mule.tooling.devkit.treeview.model.ModuleSource;
import org.mule.tooling.devkit.treeview.model.ModuleTransformer;
import org.mule.tooling.devkit.treeview.model.ProjectRoot;
import org.mule.tooling.devkit.treeview.model.Property;
import org.mule.tooling.devkit.treeview.model.Configuration;

public class ModuleVisitor extends ASTVisitor {

    private ProjectRoot root = new ProjectRoot();

    private boolean forceSearch = false;

    public ProjectRoot getRoot() {
        return root;
    }

    public void setRoot(ProjectRoot root) {
        this.root = root;
    }

    private Module parent;
    private Module module;
    private CompilationUnit compilationUnit;

    List<TypeDeclaration> connectors = new ArrayList<TypeDeclaration>();
    List<MethodDeclaration> processor = new ArrayList<MethodDeclaration>();
    List<FieldDeclaration> configurable = new ArrayList<FieldDeclaration>();

    @Override
    public boolean visit(CompilationUnit node) {
        compilationUnit = node;
        LocateAnnotationVisitor visitorConnector = new LocateAnnotationVisitor(0, ModelUtils.CONNECTOR_ANNOTATION).addAnnotation(ModelUtils.MODULE_ANNOTATION)
                .addAnnotation(ModelUtils.CONFIGURATION_ANNOTATION).addAnnotation(ModelUtils.BASIC_AUTH_ANNOTATION).addAnnotation(ModelUtils.OAUTH_ANNOTATION)
                .addAnnotation(ModelUtils.METADATA_CATEGORY_ANNOTATION).addAnnotation(ModelUtils.HTTP_BASIC_AUTH_ANNOTATION);

        node.accept(visitorConnector);
        if (visitorConnector.getNode() != null || forceSearch) {
            visitorConnector = new LocateAnnotationVisitor(0, ModelUtils.CONNECTOR_ANNOTATION).addAnnotation(ModelUtils.MODULE_ANNOTATION);
            node.accept(visitorConnector);
            if (visitorConnector.getNode() != null) {
                module = new Module(root, (ICompilationUnit) compilationUnit.getJavaElement(), node);
            } else {
                visitorConnector = new LocateAnnotationVisitor(0, ModelUtils.METADATA_CATEGORY_ANNOTATION);
                node.accept(visitorConnector);
                if (visitorConnector.getNode() != null) {
                    module = new MetaDataCategory(root, (ICompilationUnit) compilationUnit.getJavaElement(), node);
                } else {
                    if (forceSearch) {
                        module = new Module(root, (ICompilationUnit) compilationUnit.getJavaElement(), node);
                    } else {
                        module = new Configuration(root, (ICompilationUnit) compilationUnit.getJavaElement(), node);
                    }
                }
            }
            root.getModules().add(module);
            forceSearch = false;
            return true;
        }
        module = null;
        return false;
    }

    @Override
    public boolean visit(NormalAnnotation node) {
        if (node.getParent() instanceof MethodDeclaration) {
            if (ModelUtils.isAnnotationSupported(node.getTypeName())) {
                ModuleMethod method = null;
                if (ModelUtils.isTransformerMethod(node.getTypeName())) {
                    method = new ModuleTransformer(module, (ICompilationUnit) compilationUnit.getJavaElement(), node);
                } else if (ModelUtils.isSourceMethod(node.getTypeName())) {
                    method = new ModuleSource(module, (ICompilationUnit) compilationUnit.getJavaElement(), node);
                } else {
                    method = new ModuleMethod(module, (ICompilationUnit) compilationUnit.getJavaElement(), node);
                }
                method.setConnectionMethod(ModelUtils.isConnectionAnnotation(node.getTypeName()));
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

        if (!ModelUtils.isAnnotationSupported(node.getTypeName())) {
            return false;
        }

        module.setName(((TypeDeclaration) node.getParent()).getName().toString());
        module.setType("@" + node.getTypeName().toString());
        for (Object value : node.values()) {
            MemberValuePair valuePair = (MemberValuePair) value;

            Property prop = new Property(module, (ICompilationUnit) compilationUnit.getJavaElement(), valuePair);
            prop.setName(valuePair.getName().toString());
            try {
                if (valuePair.getValue() instanceof StringLiteral) {
                    prop.setValue(((StringLiteral) valuePair.getValue()).getLiteralValue());
                } else {
                    prop.setValue(valuePair.getValue().toString());
                }
            } catch (Exception ex) {
                DevkitUIPlugin.log(ex);
                prop.setValue("");
            }
            module.getProperties().add(prop);
        }

        connectors.add((TypeDeclaration) node.getParent());
        return true;
    }

    @Override
    public boolean visit(MarkerAnnotation node) {
        // SHOULD NEVER ENTER THIS IF UNLESS USERS REMOVE ALL PARAMTERS FROM CONNECTOR, THAT IS NOT VALID
        if (ModelUtils.annotationMatches(node.getTypeName(), ModelUtils.CONNECTOR_ANNOTATION)) {
            module.setName(((TypeDeclaration) node.getParent()).getName().toString());
            module.setType("@" + node.getTypeName().toString());
            connectors.add((TypeDeclaration) node.getParent());
            return true;
        }
        if (ModelUtils.annotationMatches(node.getTypeName(), ModelUtils.CONFIGURABLE_ANNOTATION)
                || ModelUtils.annotationMatches(node.getTypeName(), ModelUtils.CONNECTION_STRATEGY_ANNOTATION)) {
            configurable.add((FieldDeclaration) node.getParent());

            ModuleField field = null;
            if (ModelUtils.annotationMatches(node.getTypeName(), ModelUtils.CONFIGURABLE_ANNOTATION))
                field = new ModuleField(module, (ICompilationUnit) compilationUnit.getJavaElement(), node.getParent());
            else
                field = new ModuleFieldStragegy(module, (ICompilationUnit) compilationUnit.getJavaElement(), node.getParent());

            field.setField((FieldDeclaration) node.getParent());
            try {
                if (module == null)
                    return false;
                module.getConfigurable().add(field);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        } else if (ModelUtils.annotationMatches(node.getTypeName(), ModelUtils.METADATA_CATEGORY_ANNOTATION)) {
            module.setName(((TypeDeclaration) node.getParent()).getName().toString());
            module.setType("@" + node.getTypeName().toString());
        } else if (ModelUtils.isAnnotationSupported(node.getTypeName())) {
            ModuleMethod method = null;
            if (ModelUtils.isTransformerMethod(node.getTypeName())) {
                method = new ModuleTransformer(module, (ICompilationUnit) compilationUnit.getJavaElement(), node.getParent());
            } else if (ModelUtils.isSourceMethod(node.getTypeName())) {
                method = new ModuleSource(module, (ICompilationUnit) compilationUnit.getJavaElement(), node.getParent());
            } else {
                method = new ModuleMethod(module, (ICompilationUnit) compilationUnit.getJavaElement(), node.getParent());
                method.setConnectionMethod(ModelUtils.isConnectionAnnotation(node.getTypeName()));
            }
            method.setMetadataMethod(ModelUtils.isMetadaMethod(node.getTypeName()));
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
            if (forceSearch) {
                return true;
            }
            if (type instanceof Annotation) {
                if (node.getSuperclassType() != null) {
                    Type superType = node.getSuperclassType();
                    // TODO Handle super class to introspect processors and configurables from Parent
                    if (superType != null && superType.isSimpleType()) {
                        try {
                            IJavaElement element = superType.resolveBinding().getJavaElement();
                            if (element.getParent() instanceof IClassFile) {
                                // Class file, we have no source, so avoid parsing it
                                return true;
                            }
                            CompilationUnit parse = ASTUtils.parse((ICompilationUnit) element.getParent());
                            ModuleVisitor visitor = new ModuleVisitor();
                            visitor.forceSearch = true;
                            parse.accept(visitor);
                            if (!visitor.getRoot().getModules().isEmpty()) {
                                parent = visitor.getRoot().getModules().get(0);
                                visitor.getMethods();
                                parent.setName(element.getElementName());
                                parent.setType("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
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

    @Override
    public void endVisit(TypeDeclaration node) {
        if (parent != null) {
            for (Module mod : root.getModules()) {
                if (mod.getCompilationUnit().getElementName().equals(parent.getCompilationUnit().getElementName())) {
                    return;
                }
            }
            this.root.getModules().add(parent);
        }
    }
}