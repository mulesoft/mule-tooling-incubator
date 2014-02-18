package org.mule.tooling.ui.contribution.munit.editors;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.part.FileEditorInput;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;

/**
 * <p>
 * Matching strategy of the {@link MunitMultiPageEditor}
 * </p>
 */
public class MunitEditorMatchingStrategy implements IEditorMatchingStrategy {

    @Override
    public boolean matches(IEditorReference editorRef, IEditorInput input) {
        if (input instanceof FileEditorInput && input.getName().contains(editorRef.getPartName())) {
            IPath path = ((FileEditorInput) input).getFile().getProjectRelativePath();
            return path.toString().contains(MunitPlugin.MUNIT_FOLDER_PATH);
        }
        return false;
    }

}
