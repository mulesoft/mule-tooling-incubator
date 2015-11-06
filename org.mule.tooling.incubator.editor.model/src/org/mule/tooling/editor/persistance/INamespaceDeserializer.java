package org.mule.tooling.editor.persistance;

import org.mule.tooling.editor.model.Namespace;

public interface INamespaceDeserializer<T> {

    Namespace deserialize(T input);
}
