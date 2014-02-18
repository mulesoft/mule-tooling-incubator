package org.mule.tooling.ui.contribution.munit;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;

/**
 * <p>
 * The {@link IContentDescriber} used to determine if the file should be opened by the Munit editor
 * </p>
 */
public class MunitContentTypeDescriber implements IContentDescriber {

    @Override
    public int describe(InputStream contents, IContentDescription description) throws IOException {
        // TODO: Make this better
        if (IOUtils.toString(contents).contains(MunitPlugin.MUNIT_NAMESPACE)) {
            return IContentDescriber.VALID;
        }
        return IContentDescriber.INVALID;
    }

    @Override
    public QualifiedName[] getSupportedOptions() {
        return new QualifiedName[] {};
    }
}
