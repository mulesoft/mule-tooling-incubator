package org.mule.tooling.editor.persistance;

import org.mule.tooling.editor.model.Namespace;

public interface INamespaceSerializer<T> {

    void serialize(Namespace namespace, T ouput);
}
