package org.mule.tooling.incubator.gradle.content;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;


public class StudioGradleEnabledContentDescriber implements ITextContentDescriber {

    @Override
    public int describe(InputStream contents, IContentDescription description) throws IOException {
        
        BufferedReader rdr = new BufferedReader(new InputStreamReader(contents));
        
        String line = rdr.readLine();
        
        while (line != null) {
            if (line.contains("apply") && line.contains("plugin") && line.contains("mulestudio")) {
                return IContentDescriber.VALID;
            }
            line = rdr.readLine();
        }
        
        return IContentDescriber.INVALID;
    }

    @Override
    public QualifiedName[] getSupportedOptions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int describe(Reader contents, IContentDescription description) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }



}
