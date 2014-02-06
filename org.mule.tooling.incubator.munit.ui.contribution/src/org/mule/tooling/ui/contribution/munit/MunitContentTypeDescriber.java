package org.mule.tooling.ui.contribution.munit;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;

public class MunitContentTypeDescriber implements IContentDescriber{

	@Override
	public int describe(InputStream contents, IContentDescription description)
			throws IOException {
		if ( IOUtils.toString(contents).contains("http://www.mulesoft.org/schema/mule/munit") ){
			return IContentDescriber.VALID;
		}
		return IContentDescriber.INVALID;
	}

	@Override
	public QualifiedName[] getSupportedOptions() {
		return new QualifiedName[]{};
	}



}
