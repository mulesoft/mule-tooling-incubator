package org.mule.tooling.devkit.sample.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.part.FileEditorInput;
import org.mule.tooling.devkit.treeview.model.Module;

public class SampleDocEditorInput extends FileEditorInput {

    private final Module module;

    public SampleDocEditorInput(IFile file, Module module) {
        super(file);
        this.module = module;
    }

    public Module getModule() {
        return module;
    }

}
