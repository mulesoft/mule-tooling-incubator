/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.mule.tooling.studio.ui.editor;

import javax.xml.bind.JAXBException;

import org.eclipse.ui.IEditorInput;
import org.mule.tooling.editor.model.Namespace;
import org.mule.tooling.editor.persistance.Utils;

public class SimpleModel {

    private Namespace namespace;
    private IEditorInput input;

    public SimpleModel() {
    }

    public SimpleModel(IEditorInput input) {
        this.setInput(input);
        setNamespace(new Utils().deserialize(input));
    }

    public void fireModelChanged(Object[] objects, String type, String property) {
    }

    public Object[] getContents() throws JAXBException {
        if (getNamespace() == null) {
            setNamespace(new Utils().deserialize(input));
        }
        return new Object[] { getNamespace() };
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    public IEditorInput getInput() {
        return input;
    }

    public void setInput(IEditorInput input) {
        this.input = input;
    }
}