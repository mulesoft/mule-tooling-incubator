package org.mule.tooling.devkit.treeview.model;

import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;

public class VerifyChainNode {

    public VerifyChainNode(QualifiedName node, VerifyChainNode next) {
        super();
        this.node = node;
        this.next = next;
    }

    QualifiedName node;
    VerifyChainNode next;

    boolean isSupported(Name annotationName) {
        boolean equals = ModelUtils.annotationMatches(annotationName, node);
        if (!equals && next != null) {
            equals = next.isSupported(annotationName);
        }
        return equals;
    }

}