package org.mule.tooling.ui.contribution.debugger.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.TreeNode;

import com.mulesoft.mule.debugger.response.ObjectFieldDefinition;
import com.mulesoft.mule.debugger.response.ObjectFieldDefinitionFactory;

public class LocalObjectTreeNodeBuilder {

    public static TreeNode[] createTreeNode(Collection<ObjectFieldDefinition> objects) {
        Collection<ObjectFieldDefinition> filteredCollection = getCollectionWithoutNulls(objects);
        TreeNode[] result = new TreeNode[filteredCollection.size()];
        int i = 0;
        for (ObjectFieldDefinition objectFieldDefinition : filteredCollection) {
            result[i] = createTreeNode(objectFieldDefinition);
            i++;
        }
        return result;
    }

    public static TreeNode[] createChildrenNodes(TreeNode parent, Object element) {

        ObjectFieldDefinition fieldDefinition = (ObjectFieldDefinition) parent.getValue();
        List<ObjectFieldDefinition> innerElements = fieldDefinition.getInnerElements();
        if ((innerElements == null || innerElements.isEmpty())) {
            innerElements = new ArrayList<ObjectFieldDefinition>();
            ObjectFieldDefinition children = ObjectFieldDefinitionFactory.createFromObject(element, fieldDefinition.getPath().getRootExpression(), fieldDefinition.getPath());
            innerElements.addAll(children.getInnerElements());
            return doCreateChildrenNodes(parent, innerElements);
        } else {
            return doCreateChildrenNodes(parent, innerElements);
        }

    }

    /**
     * Creates the children treeNodes and sets them as children of their parent.
     */
    private static TreeNode[] doCreateChildrenNodes(TreeNode parent, List<ObjectFieldDefinition> innerElements) {
        TreeNode[] children = new TreeNode[innerElements.size()];
        int i = 0;
        for (ObjectFieldDefinition objectFieldDef : innerElements) {
            children[i] = createTreeNode(objectFieldDef);
            children[i].setParent(parent);
            i++;
        }
        parent.setChildren(children);
        return children;
    }

    public static TreeNode createTreeNode(ObjectFieldDefinition object) {
        if (object == null)
            throw new IllegalArgumentException("The value for the tree node can't be null");
        return new TreeNode(object);
    }

    private static Collection<ObjectFieldDefinition> getCollectionWithoutNulls(Collection<ObjectFieldDefinition> objects) {
        Collection<ObjectFieldDefinition> filteredList = new ArrayList<ObjectFieldDefinition>();
        for (ObjectFieldDefinition objectFieldDefinition : objects) {
            if (objectFieldDefinition != null)
                filteredList.add(objectFieldDefinition);
        }
        return filteredList;
    }

    
}